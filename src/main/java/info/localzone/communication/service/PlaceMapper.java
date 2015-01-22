package info.localzone.communication.service;

import info.localzone.communication.model.Place;
import info.localzone.communication.model.openstreetmap.NomatimResponse;
import info.localzone.communication.model.openstreetmap.OverpassElement;
import info.localzone.util.RedisUtils;
import info.localzone.util.StringUtils;

import java.nio.charset.CharacterCodingException;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
@Component
public class PlaceMapper {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlaceMapper.class);
	@Autowired
	private StringRedisTemplate redisTemplate;

	public Place mapFromNomatim(NomatimResponse nomatimResponse) {
		Place place = new Place();
		place.setOriginId(nomatimResponse.getPlace_id());
		StringTokenizer st = new StringTokenizer(nomatimResponse.getDisplay_name(), ",");

		String displayName = st.nextToken();
		place.setDisplay_name(displayName);
		if (nomatimResponse.getAddress().getCity() != null && !nomatimResponse.getAddress().getCity().isEmpty())
			place.getAddress().setCity(nomatimResponse.getAddress().getCity());
		else
			place.getAddress().setCity(nomatimResponse.getAddress().getCity_district());

		place.getAddress().setPostcode(nomatimResponse.getAddress().getPostcode());

		if (exists(nomatimResponse.getAddress().getRoad()))
			place.getAddress().setStreet(nomatimResponse.getAddress().getRoad());
		else if (exists(nomatimResponse.getAddress().getFootway()))
			place.getAddress().setStreet(nomatimResponse.getAddress().getFootway());

		if (exists(nomatimResponse.getAddress().getHouse_number()))
			place.getAddress().setHouse_number(nomatimResponse.getAddress().getHouse_number());
		else
			place.getAddress().setHouse_number("");
		place.setLat(nomatimResponse.getLat());
		place.setLon(nomatimResponse.getLon());
		place.setType(nomatimResponse.getType().substring(0, 1).toUpperCase() + nomatimResponse.getType().substring(1));
		return place;
	}
	Jackson2JsonRedisSerializer<OverpassElement> serializer = new Jackson2JsonRedisSerializer<OverpassElement>(OverpassElement.class);

	
	
	private Map<String, String> typeMapper =  ImmutableMap.of (
			"childcare","kindergarten",
			"brothel","null",
			"firestation","null",
			"place_of_worship;public_building","place_of_worship"
			);
	public Place mapFromOverpass(OverpassElement overpassElement) {
		Place place = new Place();
		place.setLat(overpassElement.getLat());
		place.setLon(overpassElement.getLon());
		place.setOriginId(overpassElement.getId());
		
		if (overpassElement.getType().equals("way")) {
			String firstNode = overpassElement.getNodes().get(0);
			LOGGER.debug("way " + overpassElement.getTags().get("name") + " found, first node = " + firstNode);
			if (firstNode != null ){
				String serializedValue = RedisUtils.getFromOpenStreetResultCache(redisTemplate, firstNode);
				try {
					OverpassElement refNode = serializer.deserialize(StringUtils.stringToByte(serializedValue));
					place.setLat(refNode.getLat());
					place.setLon(refNode.getLon());
				} catch (SerializationException e) {
					LOGGER.error(e.getLocalizedMessage(),e);
					return place;
				} catch (CharacterCodingException e) {
					LOGGER.error(e.getLocalizedMessage(),e);
					return place;
				}
			}
			else {
				LOGGER.error("no node found --- should not happen");

				return place;
			}
			LOGGER.debug("place lat/lon: " + place.getLat() + "/" + place.getLon());
			
		}
		if (overpassElement.getTags() == null) {
			
			return place;
		}
		place.setDisplay_name(overpassElement.getTags().get("name"));
		String type = overpassElement.getTags().get("amenity");
		if (typeMapper.containsKey(type)){
			type = typeMapper.get(type);
		
			if (type.equals("null"))
				return place;
			
		}
		else
			place.setType(type);
		
		LOGGER.debug("place type: " + place.getType());
	
		
		if (overpassElement.getTags().get("addr:city") != null && !overpassElement.getTags().get("addr:city").isEmpty())
			place.getAddress().setCity(overpassElement.getTags().get("addr:city"));
		else
			place.getAddress().setCity(overpassElement.getTags().get("addr:city_district") );

		place.getAddress().setPostcode(overpassElement.getTags().get("addr:postcode") );
		if (exists(overpassElement.getTags().get("addr:housenumber")))
			place.getAddress().setHouse_number(overpassElement.getTags().get("addr:housenumber"));
		else
			place.getAddress().setHouse_number("");
	
		place.getAddress().setStreet(overpassElement.getTags().get("addr:street"));
		place.setPhoneNumber(overpassElement.getTags().get("phone"));
		place.setWebSite(overpassElement.getTags().get("website"));
		
		
		return place;
	}

	static private boolean exists(String check) {
		return check != null && !check.isEmpty();
	}
}
