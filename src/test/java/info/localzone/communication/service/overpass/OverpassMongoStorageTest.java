package info.localzone.communication.service.overpass;

import info.localzone.communication.LocalzoneApplication;
import info.localzone.communication.model.openstreetmap.OverpassElement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LocalzoneApplication.class)

public class OverpassMongoStorageTest {
	@Autowired OverpassMongoStorageService overpassMongoStorageService;
	@Test public void testSaveAndRead () {
		OverpassElement overpassElement = new OverpassElement();
		overpassElement.setId("myId123121");
		
		try {
			overpassMongoStorageService.save(overpassElement);
			OverpassElement testOverpassElement = overpassMongoStorageService.getById("myId123121");
			Assert.notNull(testOverpassElement);
			testOverpassElement = overpassMongoStorageService.getById("31313");
			Assert.isNull(testOverpassElement);

		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
	}
}
