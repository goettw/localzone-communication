package info.localzone.communication.model;

import javax.validation.constraints.NotNull;

public class WebMessage {
	public String getStatus() {
		return status;

	}

	public void setStatus(String status) {
		this.status = status;
	}

	@NotNull
	String status;

	String from;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	double longitude, latitude;

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
}
