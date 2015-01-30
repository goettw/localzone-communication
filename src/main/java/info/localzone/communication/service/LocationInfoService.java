package info.localzone.communication.service;

import info.localzone.communication.model.BoundingBox;
import info.localzone.communication.model.Location;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.github.davidmoten.geo.Coverage;
import com.github.davidmoten.geo.GeoHash;
import com.github.davidmoten.geo.LatLong;

@Service
public class LocationInfoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocationInfoService.class);


	public BoundingBox getBoundingBox (String geoHash) {
		 String leftHash = GeoHash.left(geoHash);
		 String topHash = GeoHash.top(geoHash);
		 LatLong thisLatLng = GeoHash.decodeHash(geoHash);
		 LatLong leftLatLng = GeoHash.decodeHash(leftHash);
		 LatLong topLatLng = GeoHash.decodeHash(topHash);
		 double deltaLon = (thisLatLng.getLon() - leftLatLng.getLon())/2;
		 double deltaLat = (thisLatLng.getLat() - topLatLng.getLat())/2;
		 return new BoundingBox(new Location(thisLatLng.getLat()-deltaLat,thisLatLng.getLon()-deltaLon), new Location(thisLatLng.getLat()+deltaLat,thisLatLng.getLon()+deltaLon));
	}
	
	/*
	 * returns all sub hashes of the geoHash.
	 * This typically used to get all hashes with the standard precision and in a second step aggregate the results of the sets, contained.
	 **/
	public List<String> getGeoHashesInCircle(Location center, double radiusKm, int targetPrecision){
		BoundingBox boundingBox = new BoundingBox(center, radiusKm);
		Coverage coverage = GeoHash.coverBoundingBox(boundingBox.getTopLeftLat(), boundingBox.getTopLeftLon(), boundingBox.getBottomRightLat(), boundingBox.getBottomRightLon(), targetPrecision);		
		return new ArrayList<String>(coverage.getHashes());
	}
	
	public List<String> getGeoHashesInCircle(Location center, double radiusKm){
		BoundingBox boundingBox = new BoundingBox(center, radiusKm);
		Coverage coverage = GeoHash.coverBoundingBox(boundingBox.getTopLeftLat(), boundingBox.getTopLeftLon(), boundingBox.getBottomRightLat(), boundingBox.getBottomRightLon());
		
		return new ArrayList<String>(coverage.getHashes());
		

	}

}
