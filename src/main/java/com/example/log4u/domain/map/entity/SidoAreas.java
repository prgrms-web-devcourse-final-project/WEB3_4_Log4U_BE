package com.example.log4u.domain.map.entity;

import org.locationtech.jts.geom.Geometry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "sido_areas", schema = "public")
public class SidoAreas implements Areas {

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

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Double getLat() {
		return lat;
	}

	@Override
	public Double getLon() {
		return lon;
	}
}
