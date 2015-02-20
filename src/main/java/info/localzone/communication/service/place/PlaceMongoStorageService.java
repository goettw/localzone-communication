package info.localzone.communication.service.place;

import info.localzone.communication.model.Location;
import info.localzone.communication.model.Place;
import info.localzone.communication.repo.PlaceRepository;
import info.localzone.communication.service.LocationServiceException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;

public class PlaceMongoStorageService implements PlaceStorageService {
	@Autowired
	PlaceRepository placeRepository;
	@Autowired MongoTemplate mongoTemplate;



	@Override
	public void save(Place place) throws LocationServiceException {
		placeRepository.save(place);

	}

	@Override
	public Place getPlaceById(String placeId) throws LocationServiceException {
		return placeRepository.findPlaceById(placeId);
	}

	@Override
	public Place getPlaceByOriginId(String originId) throws LocationServiceException {
		List<Place> placeList = placeRepository.findPlaceByOriginId(originId);
		if (placeList == null || placeList.isEmpty())
			return null;
		return placeList.get(0);
	}

	@Override
	public Iterable<Place> findPlacesByRadiusAndTypes(Location location, double radius, List<String> osmChannels) {
		return placeRepository.findByPositionWithinAndTypeIn((new Circle(new Point(location.getLatitude(), location.getLongitude()), radius/111.5)),osmChannels);
	}

}
