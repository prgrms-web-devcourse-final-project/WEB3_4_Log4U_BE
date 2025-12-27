package com.example.log4u.domain.map.entity;

import org.locationtech.jts.geom.Geometry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "sido_areas", schema = "public")
public class SidoAreas  {

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

	@Column(name = "geohash")
	private String geohash;

	@Builder
	public SidoAreas(Long id, String code, String name, Double lat, Double lon, String geohash) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.geohash = geohash;
	}
}
