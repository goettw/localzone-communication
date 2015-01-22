package info.localzone.communication.model.openstreetmap;

import java.util.List;

public class OverpassResponse {
	String version,generator;
	List<OverpassElement> elements;
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getGenerator() {
		return generator;
	}
	public void setGenerator(String generator) {
		this.generator = generator;
	}
	public List<OverpassElement> getElements() {
		return elements;
	}
	public void setElements(List<OverpassElement> elements) {
		this.elements = elements;
	}
}
