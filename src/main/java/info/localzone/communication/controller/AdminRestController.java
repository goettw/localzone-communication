package info.localzone.communication.controller;

import info.localzone.communication.model.LocationZoneQuery;
import info.localzone.communication.model.LocationZoneResponse;
import info.localzone.communication.service.LocationInfoService;
import info.localzone.communication.service.OpenStreetRestClient;
import info.localzone.communication.service.PlacesService;
import info.localzone.pref.Pref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ch.hsr.geohash.GeoHash;

@RestController
public class AdminRestController {
	@Autowired LocationInfoService locationInfoService;
	@Autowired PlacesService placesService;
	@Autowired OpenStreetRestClient openStreetRestClient;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminRestController.class);
	
	@RequestMapping(value="/getLocationHashes", method = RequestMethod.POST)

	@ResponseBody List <LocationZoneResponse> getLocationHashes (@RequestBody LocationZoneQuery locationZoneQuery) {
		LOGGER.info("Location = " + locationZoneQuery.getRadius());
		List <GeoHash> geoHashList = locationInfoService.getGeoHashes(locationZoneQuery.getLocation(), locationZoneQuery.getRadius(), Pref.GEOHASH_PRECISION_NUMBEROFBITS);
		HashMap<String,String> map = new HashMap<String, String>();

		ArrayList <LocationZoneResponse> responses = new ArrayList<LocationZoneResponse>();
		for (GeoHash geoHash : geoHashList) {
			if (map.containsKey(geoHash.toBinaryString())){
					continue;
			}
			LocationZoneResponse locationZoneResponse = new LocationZoneResponse();
			locationZoneResponse.setCode(geoHash.toBinaryString());
			locationZoneResponse.setMinLatitude(geoHash.getBoundingBox().getMinLat());
			locationZoneResponse.setMinLongitude(geoHash.getBoundingBox().getMinLon());
			locationZoneResponse.setMaxLatitude(geoHash.getBoundingBox().getMaxLat());
			locationZoneResponse.setMaxLongitude(geoHash.getBoundingBox().getMaxLon());
			//LOGGER.info(locationZoneResponse.getCode()+"-"+ geoHash.significantBits()  +"-" + GeoUtils.latitudeSize(geoHash.getBoundingBox())+"-"+GeoUtils.longitudeSize(geoHash.getBoundingBox()));
			map.put(geoHash.toBinaryString(), "");
			responses.add(locationZoneResponse);
		}	
		LOGGER.info("size = " + responses.size());
		return responses;
	} 
	

	
	
}
