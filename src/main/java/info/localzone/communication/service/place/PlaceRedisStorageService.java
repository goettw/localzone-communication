package info.localzone.communication.service.place;

import info.localzone.communication.model.Location;
import info.localzone.communication.model.Place;
import info.localzone.communication.service.LocationInfoService;
import info.localzone.communication.service.LocationServiceException;
import info.localzone.pref.Pref;
import info.localzone.util.RedisLocationStoreManager;
import info.localzone.util.StringUtils;

import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.github.davidmoten.geo.GeoHash;

public class PlaceRedisStorageService implements PlaceStorageService {
	LocationInfoService locationInfoService;

	public PlaceRedisStorageService(LocationInfoService locationInfoService) {
		super();
		this.locationInfoService = locationInfoService;
	}

	@Override
	public Iterable<Place> findPlacesByRadiusAndTypes(Location location, double radius, List<String> osmChannels) throws LocationServiceException {
		List<String> geoHashList = locationInfoService.getGeoHashesInCircle(location, radius, Pref.GEOHASH_LENGTH);
		ArrayList<Place> placeList = new ArrayList<Place>();
		for (String placeId : redisLocationStoreManager.readPlacesByTypeList(geoHashList, osmChannels)) {
			placeList.add(getPlaceById(placeId));
		}
		return placeList;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PlaceRedisStorageService.class);
	@Autowired
	RedisLocationStoreManager redisLocationStoreManager;

	@Override
	public void save(Place place) throws LocationServiceException {
		if (place.getId() == null) {
			String id = redisLocationStoreManager.getPlaceId();
			place.setId(id);
		}
		savePlace(place);
	}

	@Override
	public Place getPlaceByOriginId(String originId) throws LocationServiceException {
		LOGGER.trace("looking for place by originId " + originId);

		String placeId = redisLocationStoreManager.lookupPlaceByOriginId(originId);
		if (placeId == null || placeId.equals("")) {
			LOGGER.trace("place not found: " + placeId);
			return null;
		}
		return getPlaceById(placeId);

	}

	private void savePlace(Place place) throws LocationServiceException {

		LOGGER.debug("save place: " + place.getDisplay_name() + " (ID=" + place.getId() + ")");
		// placeRepository.save(place);
		Jackson2JsonRedisSerializer<Place> serializer = new Jackson2JsonRedisSerializer<Place>(Place.class);

		try {
			String serializedObject = StringUtils.byteToString(serializer.serialize(place));
			String hash = GeoHash.encodeHash(place.getLat(), place.getLon(), Pref.GEOHASH_LENGTH);
			LOGGER.debug("writingPlace ...(type=" + place.getType() + "), hash=" + hash);
			if (place.getType() == null) {
				LOGGER.error("type should not be null here " + place.getId() + "-" + place.getOriginId() + "-" + place.getDisplay_name());
			} else {
				redisLocationStoreManager.writePlace(place, serializedObject, hash);
				redisLocationStoreManager.writeToPlaceOriginLookup(place.getOriginId(), place.getId());

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

	@Override
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
