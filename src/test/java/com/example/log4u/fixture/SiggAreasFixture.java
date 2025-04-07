package com.example.log4u.fixture;

import com.example.log4u.domain.map.entity.SiggAreas;

public class SiggAreasFixture {

	public static SiggAreas createSiggAreaFixture(Long id, String name, double lat, double lon) {
		return new SiggAreas(name, lat, lon);
	}
}
