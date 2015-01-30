package info.localzone.communication.model;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class HttpSessionBean {
	RenderedType renderedType;
	double radius = 2;
	String geoHash;

	public String getGeoHash() {
		return geoHash;
	}

	public void setGeoHash(String geoHash) {
		this.geoHash = geoHash;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public RenderedType getRenderedType() {
		return renderedType;
	}

	public void setRenderedType(RenderedType renderedType) {
		this.renderedType = renderedType;
	}

}
