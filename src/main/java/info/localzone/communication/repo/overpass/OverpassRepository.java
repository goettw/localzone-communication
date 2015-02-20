package info.localzone.communication.repo.overpass;

import info.localzone.communication.model.openstreetmap.OverpassElement;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
@Component
public interface OverpassRepository extends MongoRepository<OverpassElement, String> {
	 OverpassElement findById (String id);
}
