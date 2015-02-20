package info.localzone.communication.controller;

import info.localzone.communication.model.BoundingBox;
import info.localzone.communication.model.LocationZoneQuery;
import info.localzone.communication.model.LocationZoneResponse;
import info.localzone.communication.service.AsyncPlaceFunctions;
import info.localzone.communication.service.LocationInfoService;
import info.localzone.communication.service.OpenStreetRestClient;
import info.localzone.communication.service.PlacesService;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminRestController {
	@Autowired
	LocationInfoService locationInfoService;
	@Autowired
	PlacesService placesService;
	@Autowired
	OpenStreetRestClient openStreetRestClient;
	@Autowired
	AsyncPlaceFunctions asyncPlaceFunctions;
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminRestController.class);

	@RequestMapping(value = "/getLocationHashes", method = RequestMethod.POST)
	@ResponseBody
	List<LocationZoneResponse> getLocationHashes(@RequestBody LocationZoneQuery locationZoneQuery) {
		LOGGER.info("Location = " + locationZoneQuery.getRadius());
		LOGGER.info("Precision = " + locationZoneQuery.getPrecision());

		List<String> geoHashes = locationInfoService.getGeoHashesInCircle(locationZoneQuery.getLocation(), locationZoneQuery.getRadius(),
				locationZoneQuery.getPrecision());
		ArrayList<LocationZoneResponse> responses = new ArrayList<LocationZoneResponse>();
		LOGGER.info("size=" + geoHashes.size());
		for (String geoHash : geoHashes) {

			BoundingBox boundingBox = locationInfoService.getBoundingBox(geoHash);
			// LOGGER.info("bb="+geoHash);
			LocationZoneResponse locationZoneResponse = new LocationZoneResponse(boundingBox);
			locationZoneResponse.setColor("#aaaaaa");
			responses.add(locationZoneResponse);
		}

		return responses;
	}
/*
	@RequestMapping(value = "/admin/getPopulatedZones", method = RequestMethod.POST)
	@ResponseBody
	List<LocationZoneResponse> getPopulatedZones(@RequestBody LocationZoneQuery locationZoneQuery) {
		double radiusKm = locationZoneQuery.getRadius();
		int precision;

		if (radiusKm < 15) {
			precision = 6;
		} else if (radiusKm < 20) {
			precision = 5;

		} else {
			precision = 4;
		}
		List<String> geoHashList = locationInfoService.getGeoHashesInCircle(locationZoneQuery.getLocation(), radiusKm, precision);

		//geoHashList = placesService.getPopulatedZones(geoHashList);
		ArrayList<LocationZoneResponse> responses = new ArrayList<LocationZoneResponse>();
		for (String zoneId : geoHashList) {
			int count = placesService.channelMemberCount(zoneId, "restaurant");
			LOGGER.debug("zoneId="+zoneId + " - " + count);
			if (count == 0)
				continue;
			int density = count*2;
			LocationZoneResponse locationZoneResponse = fillLocationZoneResponse(zoneId);
			String color = "#000000";
			if (density < 3) {
				color = "#000000";
			}
			else if (density < 10) {
				color = "#00ff00";
			}
			else if (density < 20) {
				color = "#0000ff";
			}
			else {
				color = "#ff0000";
			}
			locationZoneResponse.setColor(color);
			responses.add(locationZoneResponse);
		}

		LOGGER.info("numberofpopulatedzones = " + responses.size());
		return responses;
	}*/

	/*private LocationZoneResponse fillLocationZoneResponse(String geoHash) {
		BoundingBox boundingBox = locationInfoService.getBoundingBox(geoHash);
		LocationZoneResponse locationZoneResponse = new LocationZoneResponse();
		locationZoneResponse.setCode(geoHash);

		locationZoneResponse.setMinLatitude(boundingBox.getMinLat());
		locationZoneResponse.setMinLongitude(boundingBox.getMinLon());
		locationZoneResponse.setMaxLatitude(boundingBox.getMaxLat());
		locationZoneResponse.setMaxLongitude(boundingBox.getMaxLon());

		//LOGGER.info(boundingBox.getMinLat() + "-" + boundingBox.getMinLon() + "-" + boundingBox.getMaxLat() + "-" + boundingBox.getMaxLon());
		return locationZoneResponse;
	}
*/
}
