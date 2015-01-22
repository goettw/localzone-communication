package info.localzone.communication.service;

import info.localzone.communication.model.Location;
import info.localzone.communication.model.LocationZoneQuery;
import info.localzone.communication.model.Place;
import info.localzone.communication.model.RenderedPlace;
import info.localzone.communication.model.RenderedType;
import info.localzone.pref.Pref;
import info.localzone.util.GeoUtils;
import info.localzone.util.RedisUtils;
import info.localzone.util.StringUtils;

import java.nio.charset.CharacterCodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
	@Autowired
	AsyncPlaceFunctions asyncPlaceFunctions;
	@Autowired
	MessageSource messageSource;
	public List<RenderedType> getPlaceTypesOffered(LocationZoneQuery locationZoneQuery, Locale locale) {
		GeoHash hash = GeoHash.withBitPrecision(locationZoneQuery.getLocation().getLatitude(), locationZoneQuery.getLocation().getLongitude(),
				Pref.GEOHASH_PRECISION_NUMBEROFBITS);

		List <String> typeList = new ArrayList<String>(RedisUtils.readInzonePlacesTypes(redisTemplate, 
				hash.toBinaryString(), Pref.RADIUS_FOR_INZONE_CACHE_METERS ,
				Pref.GEOHASH_PRECISION_NUMBEROFBITS)); 
		List <RenderedType> renderedTypes = new ArrayList <RenderedType>();
		LOGGER.debug("radius="+locationZoneQuery.getRadius());
		
		Collections.sort(typeList);
		for (String type : typeList) {	
			locationZoneQuery.setType(type);
			List<RenderedPlace> renderedPlaces = getPlaces(locationZoneQuery);
			if (renderedPlaces.size() > 0) {
				RenderedType renderedType = new RenderedType();
				renderedType.setType(type);
				renderedType.setCount(renderedPlaces.size());
				String displayText;
				try {
					displayText = messageSource.getMessage("placeType."+type,new Object[]{}, locale);
					renderedType.setDisplayText(displayText);
				} catch (org.springframework.context.NoSuchMessageException e) {
					LOGGER.debug(e.getLocalizedMessage(),"no localized message for " + type + " found");
					
					renderedType.setDisplayText(type);
				}
				renderedTypes.add(renderedType);
			}
		}
		
		return renderedTypes;
	}

	public List<RenderedPlace> getPlaces(LocationZoneQuery locationZoneQuery) {
		GeoHash hash = GeoHash.withBitPrecision(locationZoneQuery.getLocation().getLatitude(), locationZoneQuery.getLocation().getLongitude(),
				Pref.GEOHASH_PRECISION_NUMBEROFBITS);

		Set<String> placeIdSet = RedisUtils.readInzonePlacesCache(redisTemplate, hash.toBinaryString(), locationZoneQuery.getType(),
				Pref.RADIUS_FOR_INZONE_CACHE_METERS, Pref.GEOHASH_PRECISION_NUMBEROFBITS);

		if (placeIdSet.isEmpty()) {
			LOGGER.info("zone " + hash.toBinaryString() + " is empty, loading asynchronously");
			asyncPlaceFunctions.doBulkOverpassCatch(locationZoneQuery.getLocation());

		}
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

				DecimalFormat df = new DecimalFormat("#0.00");
				renderedPlace.setDistance(df.format(distance) + " km");
				renderedPlace.setDblDistance(distance);
				renderedPlace.setId(placeId);
				renderedPlacesList.add(renderedPlace);
			} catch (Exception e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			}

		}
		Collections.sort(renderedPlacesList);

		return renderedPlacesList;
	}

	public List<Place> getAllPlaces() {
		List<String> serializedPlaces = RedisUtils.getAllPlacesAsJsonStrings(redisTemplate);
		List<Place> placeList = new ArrayList<Place>();
		Jackson2JsonRedisSerializer<Place> serializer = new Jackson2JsonRedisSerializer<Place>(Place.class);
		Place place;

		for (String serializedPlace : serializedPlaces) {
			try {
				place = serializer.deserialize(StringUtils.stringToByte(serializedPlace));
				placeList.add(place);
			} catch (SerializationException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			} catch (CharacterCodingException e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			}
		}
		return placeList;
	}
}
