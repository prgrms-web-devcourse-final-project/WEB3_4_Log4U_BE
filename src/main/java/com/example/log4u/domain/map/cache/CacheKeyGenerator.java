package com.example.log4u.domain.map.cache;

public class CacheKeyGenerator {

	private static final String CLUSTER_CACHE_KEY = "cluster:geohash:%s:level:%d";
	private static final String MARKER_CACHE_KEY = "marker:geohash:";

	public static String clusterCacheKey(String geohash, int level) {
		return String.format(CLUSTER_CACHE_KEY, geohash, level);
	}

	public static String markerCacheKey(String geohash) {
		return MARKER_CACHE_KEY + geohash;
	}
}
