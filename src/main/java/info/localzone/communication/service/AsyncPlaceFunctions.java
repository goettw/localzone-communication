package info.localzone.communication.service;

import info.localzone.communication.model.BoundingBox;
import info.localzone.communication.model.Location;
import info.localzone.communication.model.Place;
import info.localzone.communication.model.openstreetmap.OverpassElement;
import info.localzone.communication.service.overpass.OverpassQueryLockService;
import info.localzone.communication.service.overpass.OverpassStorageService;
import info.localzone.communication.service.place.PlaceStorageService;
import info.localzone.pref.Pref;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;


public class AsyncPlaceFunctions {
	private static final Logger LOGGER = LoggerFactory.getLogger(AsyncPlaceFunctions.class);

	@Autowired
	OpenStreetRestClient openStreetRestClient;
	
	PlaceMapper placeMapper;
	@Autowired
	LocationInfoService locationInfoService;
	OverpassStorageService overpassStorageService;
	OverpassQueryLockService overpassQueryLockService;
	PlaceStorageService placeStorageService;

	@Async
	public void doBulkOverpassCatch(Location location) {
		List<String> geohashList = locationInfoService.getGeoHashesInCircle(location, Pref.RADIUS_FOR_INZONE_CACHE_METERS / 1000);

		for (String geoHash : geohashList) {
			if (!overpassQueryLockService.getOverpassQueryLock(geoHash))
				continue;
			BoundingBox bb = locationInfoService.getBoundingBox(geoHash);
			List<OverpassElement> oList = openStreetRestClient.queryAllPlacesFromOerpass(bb.getMinLat(), bb.getMinLon(), bb.getMaxLat(), bb.getMaxLon());

			Collections.sort(oList);
			for (OverpassElement overpassElement : oList) {

				try {
					if (placeMapper.isReferenceNode(overpassElement)) {
						overpassStorageService.save(overpassElement);
					} else {
						Place place = null;
						place = placeStorageService.getPlaceByOriginId(overpassElement.getId());
						if (place == null) {
							place = placeMapper.mapFromOverpass(overpassElement);

							if (place == null) {
								LOGGER.debug("place=null for osmid=" + overpassElement.getId());
								continue;
							}
							placeStorageService.save(place);
						}
					}
				} catch (LocationServiceException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
	}

	public AsyncPlaceFunctions(OpenStreetRestClient openStreetRestClient, OverpassStorageService overpassStorageService,
			OverpassQueryLockService overpassQueryLockService, PlaceStorageService placeStorageService, PlaceMapper placeMapper) {
		this.openStreetRestClient = openStreetRestClient;
		this.overpassStorageService = overpassStorageService;
		this.overpassQueryLockService = overpassQueryLockService;
		this.placeStorageService = placeStorageService;
		this.placeMapper = placeMapper;
	}
}
