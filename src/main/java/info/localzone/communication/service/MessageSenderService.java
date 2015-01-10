package info.localzone.communication.service;

import info.localzone.communication.model.Actor;
import info.localzone.communication.model.Message;
import info.localzone.pref.Pref;
import info.localzone.util.GeoUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import ch.hsr.geohash.GeoHash;

/*
 * chrome-extension://hgmloofddffdnphfgcellkdfbfbjeloo/RestClient.html#RequestPlace:saved/2
 * {"payload":{"subject":"hhhh","body":"body"},"header":{"from":{"name":"test","location":{"latitude":0.0,"longitude":0.0,"accuracy":null}},"created":1419946064955,"location":{"latitude":0.0,"longitude":0.0,"accuracy":null}},"radius":1.1999999999999999555910790149937383830547332763671875}}
 * 
 * 
 * */
@Service
public class MessageSenderService {
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageSenderService.class);

	@Autowired
	private StringRedisTemplate template;

	public String send(Message message) {
		GeoHash hash = GeoHash.withBitPrecision(message.getHeader().getLocation().getLatitude(), message.getHeader().getLocation().getLongitude(),
				Pref.GEOHASH_PRECISION_NUMBEROFBITS);

		Date created = Calendar.getInstance().getTime();
		message.getHeader().setCreated(created);

		LOGGER.info("message = " + message.getPayload().getBody() + "->" + hash.toBinaryString());


		Jackson2JsonRedisSerializer<Message> serializer = new Jackson2JsonRedisSerializer<Message>(Message.class);
	
		String serializedMessage = new String(serializer.serialize(message));

		sendToAllDestinations(hash, 3000, created, serializedMessage,message.getHeader().getFrom());

//		BoundListOperations<String, String> ops = template.boundListOps(new String(Pref.REDIS_AUTHOR_SENT + message.getHeader().getFrom().getName()));
//		ops.leftPush(serializedMessage);

		return hash.toBinaryString() + " - " + GeoUtils.latitudeSize(hash.getBoundingBox()) + "/" + GeoUtils.longitudeSize(hash.getBoundingBox());

	}

	private void sendToAllDestinations(GeoHash hash, double radius, Date created, String serializedMessage, Actor actor) {

		List<GeoHash> hashes = GeoUtils.getHashcodes(hash.toBinaryString(), radius, Pref.GEOHASH_PRECISION_NUMBEROFBITS);

		int i = 0;
		HashMap<String, String> hashmap = new HashMap<String, String>();
		for (Iterator<GeoHash> it = hashes.iterator(); it.hasNext();) {
			GeoHash fineGrainedHash = it.next();
			if (hashmap.containsKey(fineGrainedHash.toBinaryString())) {
				LOGGER.info("doublette at: " + fineGrainedHash.toBinaryString());
			} else {
				hashmap.put(fineGrainedHash.toBinaryString(), "");
			//	sendToOneDestination(fineGrainedHash, created, serializedMessage, actor);
				i++;
			}
		}
		LOGGER.debug("i=" + Integer.toString(i));

	}

/*	private void sendToOneDestination(GeoHash hash, Date created, String serializedMessage, Actor actor) {
		String key = hash.toBinaryString();
		BoundZSetOperations<String, String> zops = template.boundZSetOps(key);
		LOGGER.info("zadd: key,time,message = " + key + "," + created.getTime() + "," + GeoUtils.latitudeSize(hash.getBoundingBox()) + "/"
				+ GeoUtils.longitudeSize(hash.getBoundingBox()));
		BoundListOperations<String, String> listops = template.boundListOps(new String(Pref.REDIS_AUTHOR_SENT + actor.getName()));

		
		
		zops.add(serializedMessage, created.getTime());
		
		String oldMessage = listops.index(0);
		if (oldMessage != null) {
			LOGGER.info("zremove: key,message = " + key + "," + oldMessage);
		zops.remove(oldMessage);
		}
	}*/
}
