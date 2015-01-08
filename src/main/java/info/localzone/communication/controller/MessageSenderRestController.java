package info.localzone.communication.controller;

import info.localzone.communication.model.Message;
import info.localzone.communication.service.MessageSenderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageSenderRestController {

	@Autowired
	MessageSenderService messageSenderService;
	@RequestMapping("/sendRest")
	public @ResponseBody String sendMessage (@RequestBody Message message) {
		System.out.println ("message ... = " + message.toString());
		return messageSenderService.send(message);
	} 
	@RequestMapping("/sendRestTest")
	public @ResponseBody String sendMessage (@RequestBody String message) {
		System.out.println ("message ... = " + message.toString());
		return message;
	} 
}
