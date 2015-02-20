package info.localzone.communication.model;

public class RenderedPlace implements Comparable<RenderedPlace> {
	@Override
	public int compareTo(RenderedPlace o) {
		return o.getDblDistance() > dblDistance ? 1 : o.getDblDistance() < dblDistance ? -1 : 0;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	String distance, display_name, body, id, street,zip,city,phone,website, localizedType,osmId;

	public String getOsmId() {
		return osmId;
	}

	public void setOsmId(String osmId) {
		this.osmId = osmId;
	}

	public String getLocalizedType() {
		return localizedType;
	}

	public void setLocalizedType(String localizedType) {
		this.localizedType = localizedType;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

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
