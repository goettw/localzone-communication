package info.localzone.communication.service;

import info.localzone.communication.model.Place;
import info.localzone.pref.Pref;
import info.localzone.util.RedisUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PlacesService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlacesService.class);
	@Autowired private StringRedisTemplate redisTemplate;

	public void createPlace (Place place) {
		String key = RedisUtils.createKey(redisTemplate);

		BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(Pref.REDIS_PLACE_HM+key);
		hashOps.put("display_name",place.getDisplay_name());
	}
}
