package info.localzone.communication.model;

public class LocationZoneResponse {
	double minLongitude,minLatitude,maxLongitude,maxLatitude;
	String color;
	public LocationZoneResponse (){}
	public LocationZoneResponse (BoundingBox boundingBox) {
		this.minLatitude = boundingBox.getTopLeftLat();
		this.minLongitude = boundingBox.getTopLeftLon();
		this.maxLatitude = boundingBox.getBottomRightLat();
		this.maxLongitude = boundingBox.getBottomRightLon();
	}
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public double getMinLongitude() {
		return minLongitude;
	}

	public void setMinLongitude(double minLongitude) {
		this.minLongitude = minLongitude;
	}

	public double getMinLatitude() {
		return minLatitude;
	}

	public void setMinLatitude(double minLatitude) {
		this.minLatitude = minLatitude;
	}

	public double getMaxLongitude() {
		return maxLongitude;
	}

	public void setMaxLongitude(double maxLongitude) {
		this.maxLongitude = maxLongitude;
	}

	public double getMaxLatitude() {
		return maxLatitude;
	}

	public void setMaxLatitude(double maxLatitiude) {
		this.maxLatitude = maxLatitiude;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	String code;
}
