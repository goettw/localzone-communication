package info.localzone.communication.controller;

import info.localzone.communication.model.openstreetmap.NomatimResponse;
import info.localzone.communication.model.openstreetmap.Search;
import info.localzone.communication.service.AsyncPlaceFunctions;
import info.localzone.communication.service.OpenStreetRestClient;
import info.localzone.communication.service.PlaceMapper;
import info.localzone.communication.service.PlacesService;
import info.localzone.util.RedisLocationStoreManager;
import info.localzone.util.StringUtils;

import java.nio.charset.CharacterCodingException;
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

@Controller
public class AdminWebController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminRestController.class);
	@Autowired
	OpenStreetRestClient openStreetRestClient;
	@Autowired
	private StringRedisTemplate redisTemplate;
	@Autowired PlaceMapper placeMapper;

	@Autowired
	private PlacesService placesService;
	@Autowired
	AsyncPlaceFunctions asyncPlaceFunctions;

	@RequestMapping("/zones")
	public String zones() {
		return "admin/zones";
	}	
	
	@RequestMapping("/admin/populatedZones")
	public String populatedZones() {
		return "admin/populatedZones";
	}

	@RequestMapping("/admin/blank")
	public String blank() {
		return "admin/blank";
	}
	@RequestMapping("/admin/navbar")
	public String navbar() {
		return "admin/minimum-navbar";
	}

	@RequestMapping(value = "/searchPlaces", method = RequestMethod.GET)
	public String searchPlaces(Model model) {
		model.addAttribute(new Search());
		return "admin/searchPlaces";
	}
@Autowired RedisLocationStoreManager redisLocationStoreManager;
	@RequestMapping(value = "/searchPlaces", method = RequestMethod.POST)
	public String searchPlacesSubmit(@ModelAttribute Search search, Model model) {
		LOGGER.debug("searching for ..." + search.getName());
		model.addAttribute(search);
		List<NomatimResponse> respList = openStreetRestClient.search(search.getName());
		Jackson2JsonRedisSerializer<NomatimResponse> serializer = new Jackson2JsonRedisSerializer<NomatimResponse>(NomatimResponse.class);
		for (NomatimResponse nomatimResponse : respList) {

			try {
				String serializedObject = StringUtils.byteToString(serializer.serialize(nomatimResponse));
				LOGGER.debug("serialized=" + serializedObject);
				redisLocationStoreManager.putToOpenStreetResultCache(nomatimResponse.getPlace_id(), serializedObject);
			} catch (SerializationException e) {
				LOGGER.error(e.getStackTrace().toString());
			} catch (CharacterCodingException e) {
				LOGGER.error(e.getStackTrace().toString());
			}

		}
		model.addAttribute("respList", respList);
		return "admin/searchPlaces";
	}


}
