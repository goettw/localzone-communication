package info.localzone.communication.service.overpass;

import info.localzone.communication.model.openstreetmap.OverpassElement;
import info.localzone.communication.repo.overpass.OverpassRepository;
import info.localzone.communication.service.LocationServiceException;

import org.springframework.beans.factory.annotation.Autowired;

public class OverpassMongoStorageService implements OverpassStorageService {
	@Autowired
	OverpassRepository overpassRepository;

	@Override
	public void save(OverpassElement overpassElement) throws LocationServiceException {
		overpassRepository.save(overpassElement);

	}

	@Override
	public OverpassElement getById(String id) throws LocationServiceException {
		return overpassRepository.findById(id);
	}

}
