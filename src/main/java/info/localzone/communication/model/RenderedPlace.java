package info.localzone.communication.model;

public class RenderedPlace implements Comparable<RenderedPlace>{
@Override
	public int compareTo(RenderedPlace o) {
		
		return o.getDblDistance() > dblDistance ? 1: o.getDblDistance() < dblDistance ? -1 : 0;
	}

String distance,display_name,body,id;
public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

double dblDistance;

public double getDblDistance() {
	return dblDistance;
}

public void setDblDistance(double dblDistance) {
	this.dblDistance = dblDistance;
}

public String getDistance() {
	return distance;
}

public void setDistance(String distance) {
	this.distance = distance;
}

public String getDisplay_name() {
	return display_name;
}

public void setDisplay_name(String display_name) {
	this.display_name = display_name;
}

public String getBody() {
	return body;
}

public void setBody(String body) {
	this.body = body;
}
}
