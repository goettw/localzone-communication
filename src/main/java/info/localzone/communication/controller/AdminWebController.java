package info.localzone.communication.controller;

import info.localzone.communication.model.openstreetmap.NomatimResponse;
import info.localzone.communication.model.openstreetmap.Search;
import info.localzone.communication.service.AsyncPlaceFunctions;
import info.localzone.communication.service.OpenStreetRestClient;
import info.localzone.communication.service.PlacesService;
import info.localzone.util.RedisLocationStoreManager;
import info.localzone.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.CharacterCodingException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
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
	@Autowired
	private PlacesService placesService;
	@Autowired
	AsyncPlaceFunctions asyncPlaceFunctions;
	   @Autowired(required = false) DataSource dataSource;
	    @Autowired(required = false) RedisConnectionFactory redisConnectionFactory;
	
    @RequestMapping("/services")
    public String home(Model model) {
        Map<Class<?>, String> services = new LinkedHashMap<Class<?>, String>();
        services.put(redisConnectionFactory.getClass(), toString(redisConnectionFactory));
        model.addAttribute("services", services.entrySet());
        
    //    model.addAttribute("instanceInfo", instanceInfo);
        
        return "admin/home";
    }

    private String toString(RedisConnectionFactory redisConnectionFactory) {
        if (redisConnectionFactory == null) {
            return "<none>";
        } else {
            if (redisConnectionFactory instanceof JedisConnectionFactory) {
                JedisConnectionFactory jcf = (JedisConnectionFactory) redisConnectionFactory;
                return jcf.getHostName().toString() + ":" + jcf.getPort();
            } else if (redisConnectionFactory instanceof LettuceConnectionFactory) {
                LettuceConnectionFactory lcf = (LettuceConnectionFactory) redisConnectionFactory;
                return lcf.getHostName().toString() + ":" + lcf.getPort();
            }
            return "<unknown> " + redisConnectionFactory.getClass();
        }
    }
   
    private String stripCredentials(String urlString) {
        try {
            if (urlString.startsWith("jdbc:")) {
                urlString = urlString.substring("jdbc:".length());
            }
            URI url = new URI(urlString);
            return new URI(url.getScheme(), null, url.getHost(), url.getPort(), url.getPath(), null, null).toString();
        }
        catch (URISyntaxException e) {
            System.out.println(e);
            return "<bad url> " + urlString;
        }
    }
	
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
