package info.localzone.communication.service;

import info.localzone.communication.model.Location;
import info.localzone.communication.model.Message;
import info.localzone.communication.model.RenderedStatus;
import info.localzone.pref.Pref;
import info.localzone.util.GeoUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Service;

import ch.hsr.geohash.GeoHash;

@Service
public class MessageReceiverService {
	@Autowired
	private StringRedisTemplate template;
	private static final Logger LOGGER = LoggerFactory.getLogger(MessageReceiverService.class);

	public List<RenderedStatus> getMessages(Location location, double radius) {
		ArrayList<RenderedStatus> list = new ArrayList<RenderedStatus>();
		GeoHash hash = GeoHash.withBitPrecision(location.getLatitude(), location.getLongitude(), Pref.GEOHASH_PRECISION_NUMBEROFBITS);

		String key = hash.toBinaryString();
		BoundZSetOperations<String, String> ops = template.boundZSetOps(key);
		Set<String> messageSet = ops.reverseRange(0, -1);
		
		Jackson2JsonRedisSerializer<Message> serializer = new Jackson2JsonRedisSerializer<Message>(Message.class);

		HashMap<String, Integer> fromHash = new HashMap<String, Integer>();

		for (Iterator<String> it = messageSet.iterator(); it.hasNext();) {

			Message message = serializer.deserialize(it.next().getBytes());
			RenderedStatus renderedStatus = renderStatus(location, message, radius);
			if (renderedStatus == null)
				continue;
			LOGGER.info("message" + message.getHeader().getFrom().getName());
			if (fromHash.containsKey(message.getHeader().getFrom().getName()) ) {
				// list.remove(fromHash.get(message.getHeader().getFrom().getName()).intValue());
				LOGGER.info("doublette " + fromHash.get(message.getHeader().getFrom().getName()).intValue());
				// list.add(0,message);

			} else {
				list.add(0, renderedStatus);
				fromHash.put(message.getHeader().getFrom().getName(), new Integer(list.size() - 1));
				LOGGER.info
				
				
				
				
				
				
				("put " + new Integer(list.size() - 1).toString());
			}
		}

		return list;
	}
	
	
	
	private RenderedStatus renderStatus (Location location, Message message, double maxDistance) {
		
		RenderedStatus renderedStatus = new RenderedStatus();
		double distance = GeoUtils.distance(message.getHeader().getLocation(),location);
		if (distance > maxDistance)
			return null;
			DateFormatter dateFormater = new DateFormatter();
		dateFormater.print(message.getHeader().getCreated(), Locale.GERMANY);
		renderedStatus.setTime(dateFormater.print(message.getHeader().getCreated(), Locale.GERMANY));
		
		Date now = Calendar.getInstance().getTime();
		long timeDiff = (now.getTime() - message.getHeader().getCreated().getTime())/1000;
		if (timeDiff < 60)
			renderedStatus.setTime(timeDiff + " s");
		else if (timeDiff < 3600)
			renderedStatus.setTime(timeDiff/60 + " min");
		else
			renderedStatus.setTime(timeDiff/3600 + ":" + (timeDiff%3600)/60 + " h");
			
		
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMANY);
		numberFormat.setMinimumFractionDigits(1);
		renderedStatus.setDistance(numberFormat.format(distance) + " km");
		renderedStatus.setFrom(message.getHeader().getFrom().getName());
		renderedStatus.setStatus(message.getPayload().getBody());
		return renderedStatus;
	}

}
