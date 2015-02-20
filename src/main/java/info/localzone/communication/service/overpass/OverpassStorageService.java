package info.localzone.communication.service.overpass;

import info.localzone.communication.model.openstreetmap.OverpassElement;
import info.localzone.communication.service.LocationServiceException;

public interface OverpassStorageService {
	public void save (OverpassElement overpassElement) throws LocationServiceException;
	public OverpassElement getById (String id) throws LocationServiceException;
}
