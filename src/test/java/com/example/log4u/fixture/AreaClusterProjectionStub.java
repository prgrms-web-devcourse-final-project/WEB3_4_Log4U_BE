package com.example.log4u.fixture;

import com.example.log4u.domain.map.dto.response.AreaClusterProjection;

public class AreaClusterProjectionStub implements AreaClusterProjection {
	private final Long id;
	private final String name;
	private final Double lat;
	private final Double lon;
	private final Long diaryCount;

	public AreaClusterProjectionStub(Long id, String name, Double lat, Double lon, Long diaryCount) {
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.diaryCount = diaryCount;
	}

	@Override public Long getId() { return id; }
	@Override public String getName() { return name; }
	@Override public Double getLat() { return lat; }
	@Override public Double getLon() { return lon; }
	@Override public Long getDiaryCount() { return diaryCount; }
}

