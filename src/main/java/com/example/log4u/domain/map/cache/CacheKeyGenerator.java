package com.example.log4u.domain.map.cache;

public class CacheKeyGenerator {

	private static final String CLUSTER_CACHE_KEY_FORMAT = "cluster:geohash:%s:level:%d";
	private static final String DIARY_KEY_PREFIX = "marker:diary:";
	private static final String GEOHASH_ID_SET_PREFIX = "marker:ids:geohash:";

	public static String clusterCacheKey(String geohash, int level) {
		return String.format(CLUSTER_CACHE_KEY_FORMAT, geohash, level);
	}

	public static String diaryKey(Long diaryId) {
		return DIARY_KEY_PREFIX + diaryId;
	}

	public static String diaryIdSetKey(String geohash) {
		return GEOHASH_ID_SET_PREFIX + geohash;
	}
}

