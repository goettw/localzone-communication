package info.localzone.communication.controller;

import info.localzone.communication.model.LocationZoneQuery;
import info.localzone.communication.model.RenderedPlace;
import info.localzone.communication.service.PlacesService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PlacesRestController {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlacesRestController.class);
	@Autowired PlacesService placesService;
	@RequestMapping(value="/restGetPlaces", method = RequestMethod.POST)
	public @ResponseBody List <RenderedPlace>getPlaces (@RequestBody LocationZoneQuery locationZoneQuery) {
		LOGGER.debug("location = " + locationZoneQuery.getLocation().getLatitude() + "," + locationZoneQuery.getLocation().getLongitude());
	//	ArrayList <RenderedStatus>renderedStatusList = new ArrayList<RenderedStatus>();
	
		return placesService.getPlaces(locationZoneQuery);
	} 

}
