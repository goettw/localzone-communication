package info.localzone.communication.service.place;

import info.localzone.communication.model.Location;
import info.localzone.communication.model.Place;
import info.localzone.communication.service.LocationServiceException;

import java.util.List;
import java.util.Set;

public interface PlaceStorageService {
	
	public void save (Place place) throws LocationServiceException;
	public Place getPlaceById(String placeId) throws LocationServiceException;
	public Place getPlaceByOriginId(String originId) throws LocationServiceException;
	public Iterable<Place>  findPlacesByRadiusAndTypes(Location location, double radius,List<String> osmChannels) throws LocationServiceException;
}
