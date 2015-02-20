package info.localzone.communication.controller;

import info.localzone.communication.model.Actor;
import info.localzone.communication.model.Header;
import info.localzone.communication.model.HttpSessionBean;
import info.localzone.communication.model.Location;
import info.localzone.communication.model.Message;
import info.localzone.communication.model.Payload;
import info.localzone.communication.model.RenderedType;
import info.localzone.communication.model.WebMessage;
import info.localzone.communication.service.MessageReceiverService;
import info.localzone.communication.service.MessageSenderService;

import java.util.Locale;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes ("httpSessionBean")
public class LocalzoneWebController {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalzoneWebController.class);
	// @Autowired ApplicationInstanceInfo instanceInfo;
	@Autowired MessageReceiverService messageReceiverService;
	@Autowired(required = false) RedisConnectionFactory redisConnectionFactory;
	@Autowired MessageSenderService messageSenderService;
	@Autowired HttpSessionBean httpSessionBean;

	@Autowired MessageSource messageSource;
	
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model, Locale locale) {
		
//		Map<Class<?>, String> services = new LinkedHashMap<Class<?>, String>();
//		services.put(redisConnectionFactory.getClass(), toString(redisConnectionFactory));
//		model.addAttribute("services", services.entrySet());
		if (httpSessionBean.getRenderedType()==null)
				httpSessionBean.setRenderedType(new RenderedType("restaurant", messageSource, locale));
		model.addAttribute("httpSessionBean",httpSessionBean);
	return "index";
	}
	@RequestMapping(value = "/{channel}/index.html", method = RequestMethod.GET)
	public String channelIndex(@PathVariable String channel, Model model, Locale locale) {
		model.addAttribute("channel", messageSource.getMessage(channel, new Object[] {}, locale));	
	return "channelIndex";
	}
	@RequestMapping(value = "/{channel}/dashboard.html", method = RequestMethod.GET)
	public String channelDashboard(@PathVariable String channel, Model model, Locale locale) {
		
	return "channelDashboard";
	}
	public String toString(RedisConnectionFactory redisConnectionFactory) {
		return redisConnectionFactory.toString();
	}

	@RequestMapping(value = "/send", method = RequestMethod.GET)
	public String send(Model model) {
		model.addAttribute("message", new WebMessage());
		return "placeStandardCockpit";
	}


	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public String sendSubmit(@Valid WebMessage webMessage, BindingResult bindingResult, Model model) {
		LOGGER.info("received message status = " + webMessage.getStatus());
		model.addAttribute("message", webMessage);
		Message message = new Message();
		Header header = new Header();
		Location location = new Location();
		location.setLatitude(52.4157597);
		location.setLongitude(13.3318649);
		header.setFrom(new Actor(webMessage.getFrom(), location));
		header.setLocation(location);
		message.setHeader(header);
		Payload payload = new Payload();
		payload.setBody(webMessage.getStatus());
		message.setPayload(payload);
		messageSenderService.send(message);
		return "placeStandardCockpit";
	}



}
