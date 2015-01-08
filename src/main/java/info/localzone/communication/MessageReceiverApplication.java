package info.localzone.communication;

import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.web.filter.CharacterEncodingFilter;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class MessageReceiverApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiverApplication.class);
	  @Autowired(required = false) RedisConnectionFactory redisConnectionFactory;
	  

	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic("1*"));

		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(RedisReceiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	@Bean
	RedisReceiver receiver(CountDownLatch latch) {
		return new RedisReceiver(latch);
	}

	@Bean
	CountDownLatch latch() {
		return new CountDownLatch(1);
	}
	
	
	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	CharacterEncodingFilter characterEncodingFilter() {
	  CharacterEncodingFilter filter = new CharacterEncodingFilter();
	  filter.setEncoding("UTF-8");
	  filter.setForceEncoding(true);
	  return filter;
	}
	
	
    public static void main(String[] args) {
        SpringApplication.run(MessageReceiverApplication.class, args);
    }
}
