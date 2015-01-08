package info.localzone.communication.controller;
import info.localzone.communication.model.LocationZoneQuery;
import info.localzone.communication.model.RenderedStatus;
import info.localzone.communication.service.MessageReceiverService;

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
public class MessageRecieverRestController {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageRecieverRestController.class);

	
	@Autowired
	MessageReceiverService messageReceiverService;

	@RequestMapping(value="/restReceive", method = RequestMethod.POST)
	public @ResponseBody List <RenderedStatus>getMessages (@RequestBody LocationZoneQuery locationZoneQuery) {
		LOGGER.info("location = " + locationZoneQuery.getLocation().getLatitude() + "," + locationZoneQuery.getLocation().getLongitude());
	//	ArrayList <RenderedStatus>renderedStatusList = new ArrayList<RenderedStatus>();
	
		return messageReceiverService.getMessages(locationZoneQuery.getLocation(),locationZoneQuery.getRadius());
	} 

//	@RequestMapping("/restGetHashes")
//	public @ResponseBody List <String>getHashes (@RequestParam (value="hash",required=true) String hashBinary, 
//			@RequestParam(value="radius",required=true) double radius) {
//		return messageReceiverService.getHashcodes(hashBinary, radius);
//	} 

}
