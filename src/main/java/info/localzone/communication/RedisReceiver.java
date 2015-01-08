package info.localzone.communication;

import info.localzone.communication.model.Message;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
@Controller
public class RedisReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisReceiver.class);
@Autowired
    SimpMessagingTemplate template;
    private CountDownLatch latch;

    @Autowired
    public RedisReceiver(CountDownLatch latch) {
        this.latch = latch;
    }

    @SendTo("/topic/messages")
    public void	 receiveMessage(String jsonMessage) {
        LOGGER.info("Received <" + jsonMessage + ">");
		Jackson2JsonRedisSerializer<Message> serializer = new Jackson2JsonRedisSerializer<Message>(Message.class);

        latch.countDown();
        template.convertAndSend("/topic/messages",serializer.deserialize(jsonMessage.getBytes()));
       // return serializer.deserialize(jsonMessage.getBytes());     
    }
}