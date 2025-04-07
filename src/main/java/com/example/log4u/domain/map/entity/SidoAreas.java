package com.example.log4u.domain.map.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "sido_areas", schema = "public")
public class SidoAreas {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String code;

	@Column(nullable = false)
	private String name;

	@Column(columnDefinition = "geometry")
	private Geometry geom;

	@Column(columnDefinition = "geometry")
	private Geometry center;

	@Column(nullable = false)
	private Double lat;

	@Column(nullable = false)
	private Double lon;

	public SidoAreas(String name, double lat, double lon) {
		this.name = name;
		this.lat = lat;
		this.lon = lon;
	}
}
