package info.localzone.communication.repo;

import info.localzone.communication.model.Place;

import java.util.List;

import org.springframework.data.geo.Box;
import org.springframework.data.geo.Circle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
@Component
public interface PlaceRepository extends MongoRepository<Place, String> {
	public Place findPlaceById (String id);
	public List<Place> findPlaceByOriginId (String OriginId);
	 List<Place> findByPositionWithinAndTypeIn(Circle c, List<String> types);
	 
	   List<Place> findByPositionWithin(Box b);
}
