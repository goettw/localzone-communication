package info.localzone.communication;

import info.localzone.communication.model.ChannelList;
import info.localzone.communication.model.Place;
import info.localzone.communication.service.AsyncPlaceFunctions;
import info.localzone.communication.service.LocationInfoService;
import info.localzone.communication.service.OpenStreetRestClient;
import info.localzone.communication.service.PlaceMapper;
import info.localzone.communication.service.overpass.OverpassMongoStorageService;
import info.localzone.communication.service.overpass.OverpassQueryLockService;
import info.localzone.communication.service.overpass.OverpassRedisCacheService;
import info.localzone.communication.service.place.PlaceMongoStorageService;
import info.localzone.communication.service.place.PlaceStorageService;
import info.localzone.util.RedisLocationStoreManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.google.common.collect.ImmutableList;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableMongoRepositories
@EnableAsync
public class LocalzoneApplication {
	@Autowired(required = false)
	RedisConnectionFactory redisConnectionFactory;
	@Autowired
	MongoDbFactory mongoDbFactory;

	@Autowired
	LocationInfoService locationInfoService;

	@Bean
	public MongoTemplate mongoTemplate() {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory);
		mongoTemplate.indexOps(Place.class).ensureIndex(new GeospatialIndex("position"));

		return mongoTemplate;
	}

	@Bean
	public RedisLocationStoreManager redisLocationStoreManager(StringRedisTemplate redisTemplate) {
		return new RedisLocationStoreManager(redisTemplate);
	}

	@Bean
	OverpassQueryLockService overpassQueryLockService() {
		return new OverpassQueryLockService();
	}

	@Bean
	OverpassRedisCacheService overpassRedisCacheService() {
		return new OverpassRedisCacheService();
	}

	@Bean
	OverpassMongoStorageService overpassMongoStorageService() {
		return new OverpassMongoStorageService();
	}

	@Bean
	PlaceStorageService placeStorageService() {
		return new PlaceMongoStorageService();
	}

	@Bean
	PlaceMapper placeMapper() {
		return new PlaceMapper(overpassMongoStorageService());
	}

	@Bean
	AsyncPlaceFunctions asyncPlaceFunctions() {
		return new AsyncPlaceFunctions(new OpenStreetRestClient(), overpassMongoStorageService(), overpassQueryLockService(), placeStorageService(),
				placeMapper());
	}

	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {

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
	public HttpSessionListener httpSessionListener() {
		return new SessionListener();
	}

	@Bean
	RedisReceiver receiver(CountDownLatch latch) {
		return new RedisReceiver(latch);
	}

	@Bean
	CountDownLatch latch() {
		return new CountDownLatch(1);
	}

	@Bean(name = "channelList")
	public ChannelList channelList() {
		// return new
		// ChannelList(ImmutableList.of("family","eat","drink","business","action","relax","senior","emergency"));
		return new ChannelList(ImmutableList.of("eat", "drink", "shopping", "family", "business", "action", "relax", "emergency"));
	}

	@Bean(name = "channelToOsmMapper")
	public HashMap<String, List<String>> channelToOsmMapper() {
		HashMap<String, List<String>> hashMap = new HashMap<String, List<String>>();
		hashMap.put("family", new ArrayList<String>(ImmutableList.of("social_facility", "school", "kindergarten", "cinema", "library", "arts_centre")));
		hashMap.put("eat", new ArrayList<String>(ImmutableList.of("restaurant", "fast_food")));
		hashMap.put("shopping", new ArrayList<String>(ImmutableList.of("shop")));
		hashMap.put("drink", new ArrayList<String>(ImmutableList.of("bar", "pub", "cafe")));
		hashMap.put("business", new ArrayList<String>(ImmutableList.of("arts_centre", "cinema", "taxi", "bar", "library", "post_office")));
		hashMap.put("action", new ArrayList<String>(ImmutableList.of("nightclub")));
		hashMap.put("relax", new ArrayList<String>(ImmutableList.of("arts_centre")));
		hashMap.put("senior", new ArrayList<String>(ImmutableList.of("arts_centre", "social_facility", "retirement_home")));
		hashMap.put("emergency", new ArrayList<String>(ImmutableList.of("doctors", "pharmacy", "hospital")));
		return hashMap;
	}

	@Bean(name = "messageSource")
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setCacheSeconds(5);
		messageSource.setUseCodeAsDefaultMessage(true);
		return messageSource;
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	CharacterEncodingFilter characterEncodingFilter() {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding("UTF-8");
		filter.setForceEncoding(true);
		return filter;
	}

	@Bean
	public MongoConverter mongoConverter() throws Exception {
		MongoMappingContext mappingContext = new MongoMappingContext();
		MappingMongoConverter mappingMongoConverter = new MappingMongoConverter(mongoDbFactory, mappingContext);
		mappingMongoConverter.setMapKeyDotReplacement("#");
		return mappingMongoConverter;
	}

	public static void main(String[] args) {
		SpringApplication.run(LocalzoneApplication.class, args);
	}
}
