package com.example.log4u.domain.map.cache;

import java.time.Duration;

public class RedisTTLPolicy {
	public static final Duration MARKER_TTL = Duration.ofMinutes(60);
	public static final Duration CLUSTER_TTL = Duration.ofMinutes(60);
}
