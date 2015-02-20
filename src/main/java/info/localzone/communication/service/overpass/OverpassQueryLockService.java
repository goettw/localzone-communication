package info.localzone.communication.service.overpass;

import info.localzone.util.RedisLocationStoreManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


public class OverpassQueryLockService {
	@Autowired RedisLocationStoreManager redisLocationStoreManager;
	private static final Logger LOGGER = LoggerFactory.getLogger(OverpassQueryLockService.class);

	public  boolean getOverpassQueryLock(String hashCode) {
		LOGGER.debug("redisLocationStoreManager="+redisLocationStoreManager+ " - hashcode = " + hashCode);
		return redisLocationStoreManager.getOverpassQueryLock( hashCode);
	}
}
