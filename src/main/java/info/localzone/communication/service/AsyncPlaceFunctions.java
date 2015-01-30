package info.localzone.communication.service;

import info.localzone.communication.model.BoundingBox;
import info.localzone.communication.model.Location;
import info.localzone.communication.model.Place;
import info.localzone.communication.model.openstreetmap.OverpassElement;
import info.localzone.pref.Pref;
import info.localzone.util.RedisLocationStoreManager;
import info.localzone.util.StringUtils;

import java.nio.charset.CharacterCodingException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.github.davidmoten.geo.GeoHash;



@Component
public class AsyncPlaceFunctions {
	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncPlaceFunctions.class);

	@Autowired
	OpenStreetRestClient openStreetRestClient;
	@Autowired
	PlaceMapper placeMapper;
	@Autowired
	LocationInfoService locationInfoService;
	
	@Autowired RedisLocationStoreManager redisLocationStoreManager;


	@Async
	public void doBulkOverpassCatch(Location location) {
		Jackson2JsonRedisSerializer<OverpassElement> serializer = new Jackson2JsonRedisSerializer<OverpassElement>(OverpassElement.class);
		List<String> geohashList = locationInfoService.getGeoHashesInCircle(location, Pref.RADIUS_FOR_INZONE_CACHE_METERS / 1000);

		for (String geoHash : geohashList) {
			if (!redisLocationStoreManager.getOverpassQueryLock(geoHash))
				continue;
			BoundingBox bb = locationInfoService.getBoundingBox(geoHash);
			List<OverpassElement> oList = openStreetRestClient.queryAllPlacesFromOerpass(bb.getMinLat(), bb.getMinLon(), bb.getMaxLat(),bb.getMaxLon());

			Collections.sort(oList);

			for (OverpassElement overpassElement : oList) {
				try {
					if (placeMapper.isReferenceNode(overpassElement)) {
						String serializedObject = StringUtils.byteToString(serializer.serialize(overpassElement));
						redisLocationStoreManager.putToOpenStreetResultCache( overpassElement.getId(), serializedObject);
					} else {
						Place place = getPlaceByOriginId(overpassElement.getId());
						if (place == null) {
							place = placeMapper.mapFromOverpass(overpassElement);
							
							if (place == null) {
								LOGGER.debug("place=null for osmid="+overpassElement.getId());
								continue;
							}
							String id = redisLocationStoreManager.getPlaceId();
							place.setId(id);
							redisLocationStoreManager.writeToPlaceOriginLookup(overpassElement.getId(), id);
							savePlace(place);
						}
					}
				} catch (LocationServiceException e) {
					LOGGER.error(e.getMessage(), e);
				} catch (CharacterCodingException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}



	public void savePlace(Place place) throws LocationServiceException {

		LOGGER.debug("save place: " + place.getDisplay_name() + " (ID=" + place.getId() + ")");
		Jackson2JsonRedisSerializer<Place> serializer = new Jackson2JsonRedisSerializer<Place>(Place.class);

		try {
			String serializedObject = StringUtils.byteToString(serializer.serialize(place));
			String hash = GeoHash.encodeHash(place.getLat(), place.getLon(), Pref.GEOHASH_LENGTH);
			LOGGER.debug("writingPlace ...(type=" + place.getType() + "), hash="+hash);
			if (place.getType() == null) {
				LOGGER.error("type should not be null here " + place.getId() + "-" + place.getOriginId() + "-" + place.getDisplay_name());
			} else {
				redisLocationStoreManager.writePlace( place, serializedObject, hash);

				LOGGER.trace("... ready!");
			}

		} catch (SerializationException e) {
			LOGGER.error(e.getStackTrace().toString());
			throw new LocationServiceException(e.getLocalizedMessage(), e.fillInStackTrace());
		} catch (CharacterCodingException e) {
			LOGGER.error(e.getStackTrace().toString());
			throw new LocationServiceException(e.getLocalizedMessage(), e.fillInStackTrace());
		}

	}

	public Place getPlaceByOriginId(String originId) throws LocationServiceException {
		LOGGER.trace("looking for place by originId " + originId);

		String placeId = redisLocationStoreManager.lookupPlaceByOriginId( originId);
		if (placeId == null || placeId.equals("")) {
			LOGGER.trace("place not found: " + placeId);
			return null;
		}
		return getPlaceById(placeId);
	}

	public Place getPlaceById(String placeId) throws LocationServiceException {
		String placeString = redisLocationStoreManager.readPlace(placeId);
		if (placeString == null || placeString.equals(null))
			throw new LocationServiceException("Ups - place with id " + placeId + " not found");

		Jackson2JsonRedisSerializer<Place> serializer = new Jackson2JsonRedisSerializer<Place>(Place.class);
		try {
			Place place = serializer.deserialize(StringUtils.stringToByte(placeString));
			return place;
		} catch (SerializationException e) {
			LOGGER.error(e.getStackTrace().toString());
			throw new LocationServiceException(e.getLocalizedMessage(), e.fillInStackTrace());
		} catch (CharacterCodingException e) {
			LOGGER.error(e.getStackTrace().toString());
			throw new LocationServiceException(e.getLocalizedMessage(), e.fillInStackTrace());
		}

	}

}
