package com.example.log4u.fixture;

import com.example.log4u.domain.map.entity.SidoAreas;

public class SidoAreasFixture {

	public static SidoAreas createSidoAreaFixture(Long id, String name, double lat, double lon) {
		return new SidoAreas(name, lat, lon);
	}
}
