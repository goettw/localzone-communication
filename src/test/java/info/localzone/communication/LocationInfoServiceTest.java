package info.localzone.communication;

import info.localzone.communication.model.Location;
import info.localzone.communication.service.LocationInfoService;

import java.util.List;

import org.junit.Test;

import com.github.davidmoten.geo.GeoHash;
import com.github.davidmoten.geo.LatLong;

import static org.junit.Assert.*;


public class LocationInfoServiceTest {


	
	private void runSubHashTest(double radius, int precision) {
		LocationInfoService locationInfoService = new LocationInfoService();
		//String geoHash = GeoHash.encodeHash(new LatLong(52.39379882812,13.271484375), 28);
			
		List <String> subHashes = locationInfoService.getGeoHashesInCircle(new Location(52.39379882812,13.271484375), radius, precision);
		System.out.println("size("+radius+","+precision+")="+subHashes.size());
	}
	
	@Test
	public void testGetSubHashes() {
		runSubHashTest(3, 5);
		runSubHashTest(3, 6);
		runSubHashTest(3, 7);
		runSubHashTest(3, 8);
		runSubHashTest(30, 6);
	}
}

