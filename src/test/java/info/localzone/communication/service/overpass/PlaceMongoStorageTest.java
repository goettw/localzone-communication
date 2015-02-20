package info.localzone.communication.service.overpass;

import info.localzone.communication.LocalzoneApplication;
import info.localzone.communication.model.Place;
import info.localzone.communication.model.openstreetmap.OverpassElement;
import info.localzone.communication.service.place.PlaceMongoStorageService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LocalzoneApplication.class)

public class PlaceMongoStorageTest {
	@Autowired PlaceMongoStorageService placeMongoStorageService;
	@Test public void testSaveAndRead () {
		OverpassElement overpassElement = new OverpassElement();
		overpassElement.setId("myId123121");
		
		try {
			String id = "12123232";
			String originId = "1232";
			Place place = new Place();
			double [] position = new double[2];
			position[0]=52.516232;
			position[1]=13.2545284;
			place.setPosition(position);
			place.setId(id);
			place.setOriginId(originId);
			placeMongoStorageService.save(place);
		Place testPlace1 = placeMongoStorageService.getPlaceById(id);
			Assert.notNull(testPlace1);
			Place testPlace2 = placeMongoStorageService.getPlaceByOriginId(originId);
			Assert.notNull(testPlace2);

		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
	}
}
