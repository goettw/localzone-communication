package info.localzone.communication.model.openstreetmap;

import java.util.HashMap;
import java.util.List;

import org.springframework.data.annotation.Id;

public class OverpassElement extends OsmPosition implements Comparable<OverpassElement> {
	@Id
	String id;
	String type;

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
