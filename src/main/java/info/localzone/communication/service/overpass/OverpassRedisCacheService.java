package info.localzone.communication.service.overpass;

import info.localzone.communication.model.openstreetmap.OverpassElement;
import info.localzone.communication.service.LocationServiceException;
import info.localzone.util.RedisLocationStoreManager;
import info.localzone.util.StringUtils;

import java.nio.charset.CharacterCodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class OverpassRedisCacheService implements OverpassStorageService {
	@Autowired
	RedisLocationStoreManager redisLocationStoreManager;
	Jackson2JsonRedisSerializer<OverpassElement> serializer = new Jackson2JsonRedisSerializer<OverpassElement>(OverpassElement.class);

	
	public void save(OverpassElement overpassElement) throws LocationServiceException{
		String serializedObject;
		try {
			serializedObject = StringUtils.byteToString(serializer.serialize(overpassElement));
			redisLocationStoreManager.putToOpenStreetResultCache(overpassElement.getId(), serializedObject);
		} catch (SerializationException e) {
			throw new LocationServiceException("cannot serialize overpass element",e);
		} catch (CharacterCodingException e) {
			throw new LocationServiceException("character encoding for overpass element failed",e);
		}

	}

	@Override
	public OverpassElement getById(String id) throws LocationServiceException{
		String serializedValue = redisLocationStoreManager.getFromOpenStreetResultCache(id);
		try {
			if (serializedValue != null)
				return serializer.deserialize(StringUtils.stringToByte(serializedValue));
			return null;
		} catch (SerializationException e) {
			throw new LocationServiceException("cannot deserialize overpass element",e);
		} catch (CharacterCodingException e) {
			throw new LocationServiceException("character encoding for overpass element failed",e);
		}

	}

}
