package info.localzone.communication.service;

import info.localzone.communication.model.openstreetmap.NomatimResponse;
import info.localzone.communication.model.openstreetmap.OverpassElement;
import info.localzone.communication.model.openstreetmap.OverpassResponse;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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

	public List<NomatimResponse> search(String name) {
		RestTemplate restTemplate = new RestTemplate();

		StringBuffer sb = new StringBuffer();
		sb.append("http://nominatim.openstreetmap.org/search?format=json&q=");
		sb.append(name);
		sb.append("&addressdetails=1");
		LOGGER.debug("query=" + sb.toString());
		try {
			ResponseEntity<NomatimResponse[]> responseEntity = restTemplate.getForEntity(sb.toString(), NomatimResponse[].class);

			LOGGER.debug("body length" + responseEntity.getBody().length);
			List<NomatimResponse> respList = Arrays.asList(responseEntity.getBody());
			return respList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
private DecimalFormat getDecimalFormat () {
	DecimalFormat df = new DecimalFormat("##.#####");
	DecimalFormatSymbols dfs = new DecimalFormatSymbols();
	dfs.setDecimalSeparator('.');
	dfs.setGroupingSeparator('\'');
	df.setDecimalFormatSymbols(dfs);
	return df;
	
}
	public List<NomatimResponse> searchViewbox(String name, double lat1, double lon1, double lat2, double lon2) {
		RestTemplate restTemplate = new RestTemplate();
		DecimalFormat df = getDecimalFormat();
		StringBuffer sb = new StringBuffer();
		sb.append("http://nominatim.openstreetmap.org/search?format=json&bounded=1&polygon=0");
		sb.append("&viewbox=");

		sb.append(df.format(lon1));
		sb.append(",");
		sb.append(df.format(lat1));
		sb.append(",");
		sb.append(df.format(lon2));
		sb.append(",");
		sb.append(df.format(lat2));
		sb.append("&q=");
		sb.append(name);
		sb.append("&addressdetails=1");
		LOGGER.debug("query=" + sb.toString());
		try {
			ResponseEntity<NomatimResponse[]> responseEntity = restTemplate.getForEntity(sb.toString(), NomatimResponse[].class);

			List<NomatimResponse> respList = Arrays.asList(responseEntity.getBody());
			return respList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public List<OverpassElement> queryAllPlacesFromOerpass (double lat1, double lon1, double lat2, double lon2) {
		RestTemplate restTemplate = new RestTemplate();
		DecimalFormat df = getDecimalFormat();
		StringBuffer sb = new StringBuffer();
		sb.append("http://overpass-api.de/api/interpreter?data=[out:json];(node[amenity][name]");
		StringBuffer bb= new StringBuffer();
		bb.append("(");
		bb.append(df.format(lat1));
		bb.append(",");
		bb.append(df.format(lon1));
		bb.append(",");
		bb.append(df.format(lat2));
		bb.append(",");
		bb.append(df.format(lon2));
		bb.append(");");
		sb.append(bb);
		sb.append("way[amenity][name]");
		sb.append(bb);
		sb.append(");(._;>;);out;");
		//restTemplate.getForEntity(url, responseType, urlVariables)
		String url=sb.toString();
	//	URI expanded = new UriTemplate(url).expand(endpointUrl, sessionId); // this is what RestTemplate uses 
		
			//url=URLEncoder.encode(url);
			//url = URLDecoder.decode(url.toString(), "UTF-8"); // java.net class
			LOGGER.debug("quering+"+url);
			//URI uri = URI.create(url);
			
			ResponseEntity<OverpassResponse> responseEntity = restTemplate.getForEntity(url, OverpassResponse.class);
			List<OverpassElement> elements = responseEntity.getBody().getElements();
			LOGGER.debug("body length" + elements.size());
		return elements;
		

		
	} 
}
