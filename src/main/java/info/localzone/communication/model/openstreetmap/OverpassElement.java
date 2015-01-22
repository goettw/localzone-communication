package info.localzone.communication.model.openstreetmap;

import java.util.HashMap;
import java.util.List;

public class OverpassElement implements Comparable<OverpassElement>{
	String type, id;
	double lat,lon;
	List <String> nodes;
	public List<String> getNodes() {
		return nodes;
	}
	public void setNodes(List<String> nodes) {
		this.nodes = nodes;
	}
	HashMap <String,String> tags;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public HashMap <String,String>getTags() {
		return tags;
	}
	public void setTags(HashMap<String,String> tags) {
		this.tags = tags;
	}
	@Override
	public int compareTo(OverpassElement o) {
		return getType().compareTo(o.getType());
	}
	
}
