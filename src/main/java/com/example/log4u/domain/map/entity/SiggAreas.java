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
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "sigg_areas", schema = "public")
public class SiggAreas implements Areas {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long gid;

	@Column(name = "adm_sect_c")
	private String admSectCode;

	@Column(name = "sgg_nm")
	private String sggName;

	@Column(name = "sgg_oid")
	private Integer sggOid;

	@Column(name = "col_adm_se")
	private String colAdmSe;

	@Column(columnDefinition = "geometry")
	private Geometry geom;

	@Column(columnDefinition = "geometry")
	private Geometry center;

	private Double lat;

	private Double lon;

	@Column(name = "level")
	private String level;

	@Column(name = "parent_id")
	private Integer parentId;

	@Column(name = "geohash")
	private String geohash;

	public SiggAreas(String sggName, Double lat, Double lon) {
		this.sggName = sggName;
		this.lat = lat;
		this.lon = lon;
	}

	@Override
	public String getName() {
		return sggName;
	}

	@Override
	public Long getId() {
		return gid;
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
