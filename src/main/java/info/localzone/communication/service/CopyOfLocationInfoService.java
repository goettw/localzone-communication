package info.localzone.communication.service;

import info.localzone.communication.model.Location;
import info.localzone.util.GeoUtils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import ch.hsr.geohash.queries.GeoHashCircleQuery;
import ch.hsr.geohash.util.BoundingBoxGeoHashIterator;
import ch.hsr.geohash.util.TwoGeoHashBoundingBox;

@Service
public class CopyOfLocationInfoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CopyOfLocationInfoService.class);

	public List<GeoHash> getGeoHashes(Location location, double radius) {
		GeoHashCircleQuery circleQuery = new ch.hsr.geohash.queries.GeoHashCircleQuery(new WGS84Point(location.getLatitude(), location.getLongitude()),
				radius * 1000);

		List<GeoHash> searchHashes = circleQuery.getSearchHashes();
		return searchHashes;
	}

	/*
	 * returns all sub hashes of the geoHash.
	 * This typically used to get all hashes with the standard precision and in a second step aggregate the results of the sets, contained.
	 **/
	public List<GeoHash> getSubHashes(GeoHash geoHash, int targetPrecision){
		
		BoundingBoxGeoHashIterator bbIterator = new BoundingBoxGeoHashIterator(TwoGeoHashBoundingBox.withBitPrecision(geoHash.getBoundingBox(),
				targetPrecision));
		ArrayList <GeoHash>list = new ArrayList<GeoHash>();
		while (bbIterator.hasNext()) {
			GeoHash subhash = bbIterator.next();
			if (subhash.within(geoHash))
				list.add(subhash);
		}
		return list;
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
		LOGGER.debug("getGeoHashes (radius=" + radius + ", precision=" + precision + ")-> number of zones: " + geoHashList.size());

		return geoHashList;
	}

}
