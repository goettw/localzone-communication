package info.localzone.communication.model;

public class LocationZoneResponse {
	double minLongitude,minLatitude,maxLongitude,maxLatitude;
	
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
