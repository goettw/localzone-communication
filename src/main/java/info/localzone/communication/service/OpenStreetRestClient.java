package info.localzone.communication.service;

import info.localzone.communication.model.openstreetmap.NomatimResponse;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenStreetRestClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenStreetRestClient.class);

	public List <NomatimResponse> search(String name) {
		 RestTemplate restTemplate = new RestTemplate();

		 StringBuffer sb = new StringBuffer ();
		 sb.append("http://nominatim.openstreetmap.org/search?format=json&q=");
		 sb.append(name);
		 sb.append("&addressdetails=1");
		 LOGGER.debug("query="+sb.toString());
			try {
				ResponseEntity<NomatimResponse[]> responseEntity= restTemplate.getForEntity (sb.toString(),  NomatimResponse[].class);
				
				LOGGER.debug("body length"+responseEntity.getBody().length);
				List <NomatimResponse> respList= Arrays.asList(responseEntity.getBody());
				return respList;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
return null;
	}
}
