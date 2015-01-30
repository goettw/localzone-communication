package info.localzone.communication.service;

import info.localzone.communication.model.Location;
import info.localzone.communication.model.LocationZoneQuery;
import info.localzone.communication.model.Place;
import info.localzone.communication.model.RenderedPlace;
import info.localzone.communication.model.RenderedType;
import info.localzone.pref.Pref;
import info.localzone.util.GeoUtils;
import info.localzone.util.RedisLocationStoreManager;
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
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.stereotype.Service;

@Service
public class PlacesService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlacesService.class);
	@Autowired
	LocationInfoService locationInfoService;
	@Autowired
	AsyncPlaceFunctions asyncPlaceFunctions;
	@Autowired
	MessageSource messageSource;
	@Autowired RedisLocationStoreManager redisLocationStoreManager;
	
	public List<RenderedType> getPlaceTypesOffered(LocationZoneQuery locationZoneQuery, Locale locale) {

		List<String> geoHashList = locationInfoService.getGeoHashesInCircle(locationZoneQuery.getLocation(), locationZoneQuery.getRadius(),
				Pref.GEOHASH_LENGTH);
		List<String> hashCodeList = new ArrayList<String>();

		for (String geoHash : geoHashList) {
			hashCodeList.add(geoHash);
		}

		Set<String> typeList = redisLocationStoreManager.getAllPlaceTypes(hashCodeList); 
		
		
		
		List <RenderedType> renderedTypes = new ArrayList <RenderedType>();
		LOGGER.trace("radius="+locationZoneQuery.getRadius());
		
		
		for (String type : typeList) {	
			locationZoneQuery.setType(type);
			List<RenderedPlace> renderedPlaces = getPlaces(locationZoneQuery);
			if (renderedPlaces.size() > 0) {
				RenderedType renderedType = new RenderedType(type,messageSource, locale);
				renderedType.setCount(renderedPlaces.size());
				renderedTypes.add(renderedType);
			}
		}
		Collections.sort(renderedTypes);
		return renderedTypes;
	}


	
	public List<String> getPopulatedZones (List<String> zoneList) {
		List <String> geoHashes =  new ArrayList<String>();

		for (String zoneId : zoneList) {
			if (redisLocationStoreManager.isPopulated(zoneId))
				geoHashes.add(zoneId);
		}
		return geoHashes;
	}
	
	public List<RenderedPlace> getPlaces(LocationZoneQuery locationZoneQuery) {
		
	
		List<String> geoHashList = locationInfoService.getGeoHashesInCircle(locationZoneQuery.getLocation(), locationZoneQuery.getRadius(),
				Pref.GEOHASH_LENGTH);
		LOGGER.debug("geoHashList ready. Size:" + geoHashList.size());
		List<String> hashCodeList = new ArrayList<String>();

		for (String geoHash : geoHashList) {
			hashCodeList.add(geoHash);
		}

		Set<String> placeIdSet = redisLocationStoreManager.readPlacesByZonesAndChannel (hashCodeList, locationZoneQuery.getType()) ;	

		if (placeIdSet.isEmpty()) 
			asyncPlaceFunctions.doBulkOverpassCatch(locationZoneQuery.getLocation());

		
		ArrayList<RenderedPlace> renderedPlacesList = new ArrayList<RenderedPlace>();

		for (String placeId : placeIdSet) {
			String serializedPlace = redisLocationStoreManager.readPlace(placeId);
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
		List<String> serializedPlaces = redisLocationStoreManager.getAllPlacesAsJsonStrings();
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
	
	public void setVisitor (String sessionId, String type, String geoHash){
		//LOGGER.debug("setVisitor = " + oldHash + " / " + newHash);
		redisLocationStoreManager.setVisitor (sessionId, geoHash,type) ;
	}
}
