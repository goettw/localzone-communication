package info.localzone.communication.service;

import info.localzone.communication.model.Place;
import info.localzone.communication.model.openstreetmap.NomatimResponse;
import info.localzone.communication.model.openstreetmap.OverpassElement;
import info.localzone.util.RedisLocationStoreManager;
import info.localzone.util.StringUtils;

import java.nio.charset.CharacterCodingException;
import java.util.HashMap;
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

	private Map<String, String> typeMapper = ImmutableMap.of("childcare", "kindergarten", "brothel", "null", "firestation", "null",
			"place_of_worship;public_building", "place_of_worship");

	public Place mapFromOverpass(OverpassElement overpassElement) {
		if (overpassElement.getTags() == null) {
			return null;
		}
		Place place = new Place();
		place.setLat(overpassElement.getLat());
		place.setLon(overpassElement.getLon());
		place.setOriginId(overpassElement.getId());

		HashMap<String, String> tags = overpassElement.getTags();
		LOGGER.trace("mapFromOverpass (" + overpassElement.getId() + ")- displayName=" + getTag(tags, "name") + ", type=" + getTag(tags, "amenity") + " type="
				+ overpassElement.getType().equals("way"));

		place.getAddress().setCity(getTag(tags, "addr:city", "addr:city_district"));
		place.getAddress().setPostcode(getTag(tags, "addr:postcode"));
		place.getAddress().setHouse_number(getTag(tags, "addr:housenumber"));
		place.getAddress().setStreet(getTag(tags, "addr:street"));
		place.setPhoneNumber(getTag(tags, "phone"));
		place.setWebSite(getTag(tags, "website"));
		place.setDisplay_name(getTag(tags, "name"));

		String type = getTag(tags, "amenity");
		if (type == null)
			return null;
		if (typeMapper.containsKey(type)) {
			type = typeMapper.get(type);
			if (type.equals("null"))
				return null;

		}
		place.setType(type);

		if (overpassElement.getType().equals("way"))
			place = fillWayLocations(overpassElement, place);

		return place;
	}
	@Autowired RedisLocationStoreManager redisLocationStoreManager;


	private Place fillWayLocations(OverpassElement overpassElement, Place place) {
		String firstNode = overpassElement.getNodes().get(0);
		LOGGER.debug("way " + overpassElement.getTags().get("name") + " found, first node = " + firstNode);
		if (firstNode != null) {
			String serializedValue = redisLocationStoreManager.getFromOpenStreetResultCache(firstNode);
			if (serializedValue == null) {
				LOGGER.debug("firstNode = null for " + overpassElement.getId());
				return place;
			}
			try {

				OverpassElement refNode = serializer.deserialize(StringUtils.stringToByte(serializedValue));
				place.setLat(refNode.getLat());
				place.setLon(refNode.getLon());
			} catch (SerializationException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
				return place;
			} catch (CharacterCodingException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
				return place;
			}
		} else {
			LOGGER.error("no node found --- should not happen");

			return null;
		}
		LOGGER.debug("place lat/lon: " + place.getLat() + "/" + place.getLon());

		return place;

	}

	private String getTag(HashMap<String, String> tags, String... keys) {
		for (String key : keys) {
			if (!isTagEmpty(tags, key))
				return tags.get(key);
		}
		return "";
	}

	private boolean isTagEmpty(HashMap<String, String> tags, String key) {
		if (tags == null)
			return true;
		if (!tags.containsKey(key))
			return true;
		if (tags.get(key).isEmpty())
			return true;
		return false;
	}

	public boolean isReferenceNode(OverpassElement overpassElement) {
		HashMap<String, String> tags = overpassElement.getTags();
		if (tags == null || isTagEmpty(tags, "name") || isTagEmpty(tags, "amenity")
				|| (isTagEmpty(tags, "addr:city") && isTagEmpty(tags, "addr:city-district")))
			return true;

		return false;
	}

	static private boolean exists(String check) {
		return check != null && !check.isEmpty();
	}
}
