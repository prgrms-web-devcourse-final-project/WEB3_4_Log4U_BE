package com.example.log4u.domain.map.cache;

import java.time.Duration;

public class RedisTTLPolicy {
	public static final Duration DIARY_ID_SET_TTL = Duration.ofMinutes(60);
	public static final Duration DIARY_TTL = Duration.ofMinutes(60);
	public static final Duration CLUSTER_TTL = Duration.ofMinutes(60);
}
