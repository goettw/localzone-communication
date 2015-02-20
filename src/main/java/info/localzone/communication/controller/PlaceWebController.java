package info.localzone.communication.controller;

import info.localzone.communication.model.Place;
import info.localzone.communication.model.RenderedType;
import info.localzone.communication.model.WebMessage;
import info.localzone.communication.service.LocationServiceException;
import info.localzone.communication.service.place.PlaceStorageService;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class PlaceWebController {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlaceWebController.class);
	@Autowired PlaceStorageService placeStorageService;
	@Autowired
	MessageSource messageSource;
	@RequestMapping("placeView")
	public String placeView(@RequestParam("placeId") String placeId, Model model, Locale locale) {
		try {
			Place place = placeStorageService.getPlaceById(placeId);
			model.addAttribute("place", place);
			model.addAttribute("renderedType",new RenderedType(place.getType(),messageSource,locale));
		} catch (LocationServiceException e) {
			LOGGER.error(e.getMessage(), e);
			return "error";
		}
		return "placeView";
	}
	
	


	@RequestMapping("placeEdit")
	public String placeEdit(@RequestParam("placeId") String placeId, Model model) {
		try {
			Place place = placeStorageService.getPlaceById(placeId);
			model.addAttribute("place", place);
		} catch (LocationServiceException e) {
			LOGGER.error(e.getMessage(), e);
			return "error";
		}
		return "placeEdit";
	}
	
	@RequestMapping(params="cancel", value = "/savePlace", method = RequestMethod.POST)
	public String cancelPlaceEdit(@ModelAttribute Place place, Model model) {
		return "redirect:/placeView?placeId="+place.getId();
	}
	
	@RequestMapping(params="save", value = "/savePlace", method = RequestMethod.POST)
	public String savePlace(@ModelAttribute Place place, Model model) {
		try {
			placeStorageService.save(place);
			model.addAttribute("place", place);
			model.addAttribute("message", new WebMessage());
		} catch (LocationServiceException e) {
			LOGGER.error(e.getStackTrace().toString());
			ErrorMessage errorMessage = new ErrorMessage(e);
			model.addAttribute("errorMessage", errorMessage);
			return "error";
		}
		return "redirect:/placeView?placeId="+place.getId();
	}
}
