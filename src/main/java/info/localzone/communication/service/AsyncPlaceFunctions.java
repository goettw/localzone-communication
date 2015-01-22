package info.localzone.communication.service;

import info.localzone.communication.model.Location;
import info.localzone.communication.model.Place;
import info.localzone.communication.model.openstreetmap.NomatimResponse;
import info.localzone.communication.model.openstreetmap.OverpassElement;
import info.localzone.pref.Pref;
import info.localzone.util.RedisUtils;
import info.localzone.util.StringUtils;

import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import ch.hsr.geohash.GeoHash;

@Component
public class AsyncPlaceFunctions {
	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncPlaceFunctions.class);
//	@Autowired PlacesService placesService;
	@Autowired OpenStreetRestClient openStreetRestClient;
	@Autowired PlaceMapper placeMapper;
	@Autowired StringRedisTemplate redisTemplate;
	@Autowired
	LocationInfoService locationInfoService;

	@Async 
	public void doBulkOverpassCatch (Location location) {
		Jackson2JsonRedisSerializer<OverpassElement> serializer = new Jackson2JsonRedisSerializer<OverpassElement>(OverpassElement.class);
		List<GeoHash> geohashList = locationInfoService.getGeoHashes(location, Pref.RADIUS_FOR_INZONE_CACHE_METERS / 1000);
		
	 	for (GeoHash geoHash : geohashList) {
	 		List <OverpassElement> oList = openStreetRestClient.queryAllPlacesFromOerpass(geoHash.getBoundingBox().getMinLat(), geoHash.getBoundingBox().getMinLon(), geoHash.getBoundingBox().getMaxLat(), geoHash.getBoundingBox().getMaxLon());
	 		Collections.sort(oList);
	 		for (OverpassElement overpassElement : oList) {
				LOGGER.debug("->"+overpassElement.getType()+", "+ overpassElement.getId()) ;
	 		}
	 		
	 		for (OverpassElement overpassElement : oList) {
				try {
					Place place = getPlaceByOriginId(overpassElement.getId());
					if (place == null) {
						
						place = placeMapper.mapFromOverpass(overpassElement);

						if (!(place.getAddress().getStreet() == null || place.getDisplay_name() == null || place.getType() == null)) {
							LOGGER.debug("save: " + place.getDisplay_name() + " / " + place.getAddress().getStreet()) ;
							savePlace(place);
						}
						else {
							String serializedObject = StringUtils.byteToString(serializer.serialize(overpassElement));
							LOGGER.debug("cache: " + place.getId()) ;
							RedisUtils.putToOpenStreetResultCache(redisTemplate, overpassElement.getId(), serializedObject);
						}
						
					}
				} catch (LocationServiceException e) {
					LOGGER.error(e.getMessage(),e);
				}
			 catch (CharacterCodingException e) {
				LOGGER.error(e.getMessage(),e);
			}
	 			
	 		}
	 	}		
	}
	@Async
	public void doBulkNomatimCatch (List<GeoHash> geohashList, String name) {
		 	for (GeoHash geoHash : geohashList) {
			List <NomatimResponse> nList = openStreetRestClient.searchViewbox(name,geoHash.getBoundingBox().getMinLat(), geoHash.getBoundingBox().getMinLon(), geoHash.getBoundingBox().getMaxLat(), geoHash.getBoundingBox().getMaxLon());
			for (NomatimResponse nomatimResponse : nList) {
				try {
					Place place = getPlaceByOriginId(nomatimResponse.getPlace_id());

					if (place == null) {
						place = placeMapper.mapFromNomatim(nomatimResponse);			
						LOGGER.debug("save: " + place.getDisplay_name());
						//LOGGER.debug(place.getType());
						//LOGGER.debug(place.getAddress().getCity());
						savePlace(place);
						LOGGER.debug("ready");
					}
				} catch (LocationServiceException e) {
					LOGGER.error(e.getMessage(),e);
				}

			}
		}

	}
	
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

			Location location = new Location(place.getLat(), place.getLon());
			List<GeoHash> geoHashList = locationInfoService.getGeoHashes(location, Pref.RADIUS_FOR_INZONE_CACHE_METERS / 1000,
					Pref.GEOHASH_PRECISION_NUMBEROFBITS);
			LOGGER.debug("geoHashList ready. Size:" + geoHashList.size());
			List<String> hashCodeList = new ArrayList<String>();

			for (GeoHash geoHash : geoHashList) {
				hashCodeList.add(geoHash.toBinaryString());
			}

			LOGGER.debug("writingPlace ...(type=" + place.getType() + ")");
			RedisUtils.writePlace(redisTemplate, place, serializedObject, hash.toBinaryString());
			LOGGER.debug("... ready!");
			LOGGER.debug("writingToInzoneCache ... (type=" + place.getType() + ")");
			RedisUtils.writeToInzoneCache(redisTemplate, place.getId(), place.getType(), hashCodeList, Pref.RADIUS_FOR_INZONE_CACHE_METERS,
					Pref.GEOHASH_PRECISION_NUMBEROFBITS);
			LOGGER.debug("... ready!");
			LOGGER.debug("writeToPlaceOriginLookup ...");

			if (place.getOriginId() == null || place.getOriginId().equals(""))
				LOGGER.debug("place has no originId");
			else
				RedisUtils.writeToPlaceOriginLookup(redisTemplate, place.getOriginId(), id);
			LOGGER.debug("... ready!");

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
		return getPlaceById(placeId);
	}
	public Place getPlaceById(String placeId) throws LocationServiceException {
		String placeString = RedisUtils.readPlace(redisTemplate, placeId);
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
