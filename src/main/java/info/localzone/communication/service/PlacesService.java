package info.localzone.communication.service;

import info.localzone.communication.model.Location;
import info.localzone.communication.model.LocationZoneQuery;
import info.localzone.communication.model.Place;
import info.localzone.communication.model.RenderedPlace;
import info.localzone.pref.Pref;
import info.localzone.util.GeoUtils;
import info.localzone.util.RedisUtils;
import info.localzone.util.StringUtils;

import java.nio.charset.CharacterCodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

import ch.hsr.geohash.GeoHash;

@Service
public class PlacesService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlacesService.class);
	@Autowired
	private StringRedisTemplate redisTemplate;
	@Autowired
	LocationInfoService locationInfoService;

	public void savePlace(Place place) throws LocationServiceException {
		String id = place.getId();
		if (id == null || id.equals("")) {
			id = RedisUtils.getPlaceId(redisTemplate);
			place.setId(id);
		}
		LOGGER.debug("save place: " + place.getDisplay_name() + " (ID=" + place.getId() + ")");
		Jackson2JsonRedisSerializer<Place> serializer = new Jackson2JsonRedisSerializer<Place>(Place.class);

		try {
			String serializedObject = StringUtils.byteToString(serializer.serialize(place));
			GeoHash hash = GeoHash.withBitPrecision(place.getLat(), place.getLon(), Pref.GEOHASH_PRECISION_NUMBEROFBITS);

			Location location = new Location(place.getLat(), place.getLat());
			List<GeoHash> geoHashList = locationInfoService.getGeoHashes(location, Pref.RADIUS_FOR_INZONE_CACHE_METERS / 1000,
					Pref.GEOHASH_PRECISION_NUMBEROFBITS);
			List<String> hashCodeList = new ArrayList<String>();

			for (GeoHash geoHash : geoHashList) {
				hashCodeList.add(geoHash.toBinaryString());
			}

			RedisUtils.writePlace(redisTemplate, place, serializedObject, hash.toBinaryString());
			RedisUtils.writeToInzoneCache(redisTemplate, place.getId(), hashCodeList, Pref.RADIUS_FOR_INZONE_CACHE_METERS, Pref.GEOHASH_PRECISION_NUMBEROFBITS);

			if (place.getOriginId() == null || place.getOriginId().equals(""))
				LOGGER.debug("place has no originId");
			else
				RedisUtils.writeToPlaceOriginLookup(redisTemplate, place.getOriginId(), id);

		} catch (SerializationException e) {
			LOGGER.error(e.getStackTrace().toString());
			throw new LocationServiceException(e.getLocalizedMessage(), e.fillInStackTrace());
		} catch (CharacterCodingException e) {
			LOGGER.error(e.getStackTrace().toString());
			throw new LocationServiceException(e.getLocalizedMessage(), e.fillInStackTrace());
		}

	}

	public Place getPlaceByOriginId(String originId) throws LocationServiceException {
		LOGGER.debug("looking for place by originId " + originId);

		String placeId = RedisUtils.lookupPlaceByOriginId(redisTemplate, originId);
		if (placeId == null || placeId.equals("")) {
			LOGGER.debug("place not found: " + placeId);
			return null;
		}

		String placeString = RedisUtils.readPlace(redisTemplate, placeId);
		if (placeString == null || placeString.equals(null))
			throw new LocationServiceException("Ups - place with id " + placeId + " not found although origin lookup for : " + originId
					+ " said that place exists");

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

	public List<RenderedPlace> getPlaces(LocationZoneQuery locationZoneQuery) {
		GeoHash hash = GeoHash.withBitPrecision(locationZoneQuery.getLocation().getLatitude(), locationZoneQuery.getLocation().getLatitude(),
				Pref.GEOHASH_PRECISION_NUMBEROFBITS);

		Set<String> placeIdSet = RedisUtils.readInzonePlacesCache(redisTemplate, hash.toBinaryString(), Pref.RADIUS_FOR_INZONE_CACHE_METERS,
				Pref.GEOHASH_PRECISION_NUMBEROFBITS);

		ArrayList<RenderedPlace> renderedPlacesList = new ArrayList<RenderedPlace>();

		for (String placeId : placeIdSet) {
			String serializedPlace = RedisUtils.readPlace(redisTemplate, placeId);
			Jackson2JsonRedisSerializer<Place> serializer = new Jackson2JsonRedisSerializer<Place>(Place.class);
			Place place;
			try {
				place = serializer.deserialize(StringUtils.stringToByte(serializedPlace));
				double distance = GeoUtils.distance(new Location(place.getLat(), place.getLon()), locationZoneQuery.getLocation());
				if (distance > locationZoneQuery.getRadius())
					continue;
				RenderedPlace renderedPlace = new RenderedPlace();
				renderedPlace.setDisplay_name(place.getDisplay_name());
				renderedPlace.setBody(place.getAddress().getStreet() + " " + place.getAddress().getHouse_number());

				DecimalFormat df = new DecimalFormat("#.00");
				renderedPlace.setDistance(df.format(distance) + " km");
				renderedPlace.setDblDistance(distance);

				renderedPlacesList.add(renderedPlace);
			} catch (Exception e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			}

		}
		Collections.sort(renderedPlacesList);

		return renderedPlacesList;
	}
}
