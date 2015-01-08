package info.localzone.communication.controller;

import info.localzone.communication.model.Place;
import info.localzone.communication.model.WebMessage;
import info.localzone.communication.model.openstreetmap.NomatimResponse;
import info.localzone.communication.model.openstreetmap.Search;
import info.localzone.communication.service.OpenStreetRestClient;
import info.localzone.pref.Pref;
import info.localzone.util.RedisUtils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminWebController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminRestController.class);
	@Autowired
	OpenStreetRestClient openStreetRestClient;
	@Autowired
	private StringRedisTemplate redisTemplate;

	@RequestMapping("/zones")
	public String zones() {
		return "admin/zones";
	}

	@RequestMapping(value = "/searchPlaces", method = RequestMethod.GET)
	public String searchPlaces(Model model) {
		model.addAttribute(new Search());
		return "admin/searchPlaces";
	}

	@RequestMapping(value = "/searchPlaces", method = RequestMethod.POST)
	public String searchPlacesSubmit(@ModelAttribute Search search, Model model) {
		LOGGER.debug("searching for ..." + search.getName());
		model.addAttribute(search);
		List<NomatimResponse> respList = openStreetRestClient.search(search.getName());
		Jackson2JsonRedisSerializer<NomatimResponse> serializer = new Jackson2JsonRedisSerializer<NomatimResponse>(NomatimResponse.class);
		Charset charset = Charset.forName("UTF-8");
		CharsetDecoder decoder = charset.newDecoder();
		for (NomatimResponse nomatimResponse : respList) {

			try {

				String serializedObject = decoder.decode(ByteBuffer.wrap(serializer.serialize(nomatimResponse))).toString();
				LOGGER.debug("serialized=" + serializedObject);
				RedisUtils.putToCache(redisTemplate, Pref.REDIS_OPENSTREETMAP_CACHEENTRY + nomatimResponse.getPlace_id(), serializedObject);
			} catch (SerializationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CharacterCodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		model.addAttribute("respList", respList);
		return "admin/searchPlaces";
	}

	@RequestMapping(value = "/loadPlaceFromCache", method = RequestMethod.GET)
	public String loadPlaceFromCache(@RequestParam String placeId, Model model) {
		LOGGER.debug("load place " + placeId + " from cache ...");
		Jackson2JsonRedisSerializer<NomatimResponse> serializer = new Jackson2JsonRedisSerializer<NomatimResponse>(NomatimResponse.class);
		String serializedObject = RedisUtils.getFromCache(redisTemplate, Pref.REDIS_OPENSTREETMAP_CACHEENTRY + placeId);
		LOGGER.debug("serializedObject = " + serializedObject);
		Charset charset = Charset.forName("UTF-8");
		CharsetEncoder encoder = charset.newEncoder();
		ByteBuffer byteBuffer = ByteBuffer.wrap(serializedObject.getBytes());
		CharBuffer charBuffer = CharBuffer.wrap(serializedObject.toCharArray());
		try {
			byteBuffer = encoder.encode(charBuffer);

			NomatimResponse nomatimResponse = serializer.deserialize(byteBuffer.array());
			LOGGER.debug("nomatimResponse.display_Name = " + nomatimResponse.getDisplay_name());
			Place place = new Place();
			place.setDisplay_name(nomatimResponse.getDisplay_name());
			place.getAddress().setCity(nomatimResponse.getAddress().getCity());
			place.getAddress().setPostcode(nomatimResponse.getAddress().getPostcode());
			place.getAddress().setStreet(nomatimResponse.getAddress().getRoad());
			place.getAddress().setHouse_number(nomatimResponse.getAddress().getHouse_number());
			place.setLat(nomatimResponse.getLat());
			place.setLon(nomatimResponse.getLon());
			model.addAttribute("place", place);
		} catch (CharacterCodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "placeEdit";
	}

	@RequestMapping(value = "/savePlace", method = RequestMethod.POST)
	public String savePlace(@ModelAttribute Place place, Model model) {
		model.addAttribute("place", place);
		model.addAttribute("message", new WebMessage());
		return "send";

	}

}
