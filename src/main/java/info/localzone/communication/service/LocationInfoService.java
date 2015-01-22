package info.localzone.communication.service;

import info.localzone.communication.model.Location;
import info.localzone.util.GeoUtils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.queries.GeoHashCircleQuery;
import ch.hsr.geohash.util.BoundingBoxGeoHashIterator;
import ch.hsr.geohash.util.TwoGeoHashBoundingBox;

@Service
public class LocationInfoService {
	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(LocationInfoService.class);

	public List<GeoHash> getGeoHashes(Location location, double radius) {
		ArrayList<GeoHash> geoHashList = new ArrayList<GeoHash>();
		GeoHashCircleQuery circleQuery = new ch.hsr.geohash.queries.GeoHashCircleQuery(new WGS84Point(location.getLatitude(), location.getLongitude()),
				radius * 1000);

		List<GeoHash> searchHashes = circleQuery.getSearchHashes();
		return searchHashes;
	}

	public List<GeoHash> getGeoHashes(Location location, double radius, int precision) {
		ArrayList<GeoHash> geoHashList = new ArrayList<GeoHash>();
		GeoHashCircleQuery circleQuery = new ch.hsr.geohash.queries.GeoHashCircleQuery(new WGS84Point(location.getLatitude(), location.getLongitude()),
				radius * 1000);

		List<GeoHash> searchHashes = circleQuery.getSearchHashes();

		for (GeoHash searchHash : searchHashes) {
			// geoHashList.add(searchHash);

			BoundingBoxGeoHashIterator bbIterator = new BoundingBoxGeoHashIterator(TwoGeoHashBoundingBox.withBitPrecision(searchHash.getBoundingBox(),
					precision));
			while (bbIterator.hasNext()) {
				GeoHash thisHash = bbIterator.next();
				double distance1 = GeoUtils.distance(location.getLatitude(), location.getLongitude(), thisHash.getBoundingBox().getMinLat(), thisHash
						.getBoundingBox().getMinLon(), "K");
				double distance2 = GeoUtils.distance(location.getLatitude(), location.getLongitude(), thisHash.getBoundingBox().getMinLat(), thisHash
						.getBoundingBox().getMaxLon(), "K");
				double distance3 = GeoUtils.distance(location.getLatitude(), location.getLongitude(), thisHash.getBoundingBox().getMaxLat(), thisHash
						.getBoundingBox().getMinLon(), "K");
				double distance4 = GeoUtils.distance(location.getLatitude(), location.getLongitude(), thisHash.getBoundingBox().getMaxLat(), thisHash
						.getBoundingBox().getMaxLon(), "K");

				if (distance1 > radius && distance2 > radius && distance3 > radius && distance4 > radius)
					continue;
				if (searchHash.within(thisHash))
					continue;

				geoHashList.add(thisHash);
			}
		}

		return geoHashList;
	}

}
