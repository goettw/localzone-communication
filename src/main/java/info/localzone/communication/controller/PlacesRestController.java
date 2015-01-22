package info.localzone.communication.controller;

import info.localzone.communication.model.LocationZoneQuery;
import info.localzone.communication.model.Place;
import info.localzone.communication.model.RenderedPlace;
import info.localzone.communication.model.RenderedType;
import info.localzone.communication.service.AsyncPlaceFunctions;
import info.localzone.communication.service.LocationServiceException;
import info.localzone.communication.service.PlacesService;

import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlacesRestController {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlacesRestController.class);
	@Autowired PlacesService placesService;
	@Autowired
	AsyncPlaceFunctions asyncPlaceFunctions;

	@RequestMapping(value="/restGetPlaces", method = RequestMethod.POST)
	public @ResponseBody List <RenderedPlace>getPlaces (@RequestBody LocationZoneQuery locationZoneQuery) {
		LOGGER.debug("location = " + locationZoneQuery.getLocation().getLatitude() + "," + locationZoneQuery.getLocation().getLongitude() + ", " + locationZoneQuery.getType());
	//	ArrayList <RenderedStatus>renderedStatusList = new ArrayList<RenderedStatus>();
	
		return placesService.getPlaces(locationZoneQuery);
	} 
	
	
	@RequestMapping(value="/restGetOfferedTypes", method = RequestMethod.POST)
	public @ResponseBody List <RenderedType>getOfferedTypes (@RequestBody LocationZoneQuery locationZoneQuery, Locale locale) {
		LOGGER.debug("location = " + locationZoneQuery.getLocation().getLatitude() + "," + locationZoneQuery.getLocation().getLongitude() + ", " + locationZoneQuery.getType());
	//	ArrayList <RenderedStatus>renderedStatusList = new ArrayList<RenderedStatus>();
	
		return placesService.getPlaceTypesOffered(locationZoneQuery, locale);
	} 

	@RequestMapping(value="/restGetAllPlaces", method = RequestMethod.GET)
	public @ResponseBody List <Place>getAllPlaces () {
		return placesService.getAllPlaces();
	}
	
	@RequestMapping(value = "/restWriteAllPlaces", method = RequestMethod.POST)
	public void restWriteAllPlaces(@RequestBody List<Place>placesList) {
		for (Place place : placesList) {
			try {
				asyncPlaceFunctions.savePlace(place);
			} catch (LocationServiceException e) {
				LOGGER.error(e.getLocalizedMessage(),e);
			}
		}
	}
}
