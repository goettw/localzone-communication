package info.localzone.communication.controller;

import info.localzone.communication.model.ChannelList;
import info.localzone.communication.model.HttpSessionBean;
import info.localzone.communication.model.Location;
import info.localzone.communication.model.RenderedChannel;
import info.localzone.communication.model.RenderedPlace;
import info.localzone.communication.service.AsyncPlaceFunctions;
import info.localzone.communication.service.PlaceServiceException;
import info.localzone.communication.service.PlacesService;
import info.localzone.pref.Pref;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
	@Autowired HttpSessionBean httpSessionBean;
	@Autowired
	MessageSource messageSource;
	
	@Autowired ChannelList channelList;




	@RequestMapping(value="/restGetChannels", method = RequestMethod.GET) 
	public @ResponseBody List<RenderedChannel> restGetChannels (Locale locale) {
		ArrayList <RenderedChannel> localizedChannelList = new ArrayList<RenderedChannel> ();
		LOGGER.info("restGetChannelsCalled");
		for (String channelName : channelList) 
			localizedChannelList.add(new RenderedChannel(channelName, messageSource, locale));
		return localizedChannelList;
	}
	

	@RequestMapping(value="/{channel}/restGetPlaces", method = RequestMethod.GET)
	public @ResponseBody List <RenderedPlace>getPlacesRest (@PathVariable String channel, @ModelAttribute("lat") String lat, @ModelAttribute("lon") String lon, Locale locale) {
		LOGGER.debug("channel="+channel+","+"lon="+lon+"lat="+lat);
		LOGGER.debug("locale: "+locale.toLanguageTag());
		//LOGGER.debug("-----"+((ResourceBundleMessageSource)messageSource).toString());
		try {
			return placesService.getPlaces(channel,new Location(Double.parseDouble(lat),Double.parseDouble(lon)),Pref.MIN_NUMBER_OF_ENTRIES, locale);
		} catch (NumberFormatException e) {

			LOGGER.error(e.getLocalizedMessage(),e);
			 return new ArrayList<RenderedPlace>();	
		} catch (PlaceServiceException e) {
			LOGGER.error(e.getLocalizedMessage(),e);
			 return new ArrayList<RenderedPlace>();	
			
		}
	}
	



	
	
	
	
}
