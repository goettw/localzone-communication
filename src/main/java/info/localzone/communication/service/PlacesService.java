package info.localzone.communication.service;

import info.localzone.communication.model.Location;
import info.localzone.communication.model.Place;
import info.localzone.communication.model.RenderedPlace;
import info.localzone.communication.model.RenderedType;
import info.localzone.communication.service.place.PlaceStorageService;
import info.localzone.pref.Pref;
import info.localzone.util.GeoUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
	@Autowired
	PlaceStorageService placeStorageService;
	@Autowired
	HashMap<String, List<String>> channelToOsmMapper;

	/*public List<String> getPopulatedZones(List<String> zoneList) {
		List<String> geoHashes = new ArrayList<String>();

		for (String zoneId : zoneList) {
			LOGGER.debug("zoneID=" + zoneId);
			if (redisLocationStoreManager.isPopulated(zoneId)) {
				geoHashes.add(zoneId);
			}
		}
		return geoHashes;
	}*/

	/*public int channelMemberCount(String zoneId, String channelId) {
		return redisLocationStoreManager.channelMemberCount(zoneId, channelId);
	}
*/
	public List<RenderedPlace> getPlaces(String channel, Location location, int numberOfNeededPlaces, Locale locale) throws PlaceServiceException {
		asyncPlaceFunctions.doBulkOverpassCatch(location);
		double radius = 3;
	
		List<String> osmChannels = channelToOsmMapper.get(channel);
		List<RenderedPlace> renderedPlaceList;
		if (osmChannels == null) {
			throw new PlaceServiceException("no mapping for " + channel + " found in channelToOsmMapper");
		}
		do {
			Iterable<Place> placeSet;
			try {
				placeSet = placeStorageService.findPlacesByRadiusAndTypes(location, radius, osmChannels);
			} catch (LocationServiceException e) {
				throw new PlaceServiceException(e.getLocalizedMessage());
			}
					
			renderedPlaceList = createRenderedPlaces(placeSet, location, radius, locale);
			radius += 2;
		} while (renderedPlaceList.size() < numberOfNeededPlaces && radius <= Pref.MAX_RADIUS_KM);
		return renderedPlaceList;
	}


	private List<RenderedPlace> createRenderedPlaces(Iterable<Place> placeSet, Location location, double maxRadius, Locale locale) {
		ArrayList<RenderedPlace> renderedPlacesList = new ArrayList<RenderedPlace>();

		for (Place place : placeSet) {
		
			try {
						
				double distance = GeoUtils.distance(new Location(place.getLat(), place.getLon()), location);

				if (distance > maxRadius)
					continue;

				RenderedPlace renderedPlace = new RenderedPlace();
				renderedPlace.setDisplay_name(place.getDisplay_name());
				renderedPlace.setBody(place.getAddress().getStreet() + " " + place.getAddress().getHouse_number());
				renderedPlace.setPhone(place.getPhoneNumber());
				renderedPlace.setWebsite(place.getWebSite());
				renderedPlace.setCity(place.getAddress().getCity());
				renderedPlace.setZip(place.getAddress().getPostcode());
				renderedPlace.setStreet(place.getAddress().getStreet() + " " + place.getAddress().getHouse_number());
				DecimalFormat df = new DecimalFormat("#0.00");
				renderedPlace.setDistance(df.format(distance) + " km");
				renderedPlace.setLocalizedType(new RenderedType(place.getType(), messageSource, locale).getDisplayText());
				renderedPlace.setDblDistance(distance);
				renderedPlace.setId(place.getId());
				renderedPlace.setOsmId(place.getOriginId());

				renderedPlacesList.add(renderedPlace);

			} catch (Exception e) {
				LOGGER.error(e.getLocalizedMessage(), e);
			}

		}
	//	Collections.sort(renderedPlacesList);
		return renderedPlacesList;
	}

	/*public List<Place> getAllPlaces() {
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

	public void setVisitor(String sessionId, String type, String geoHash) {
		// LOGGER.debug("setVisitor = " + oldHash + " / " + newHash);
		redisLocationStoreManager.setVisitor(sessionId, geoHash, type);
	}*/
}
