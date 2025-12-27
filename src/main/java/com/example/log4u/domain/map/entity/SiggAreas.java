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
@Table(name = "sigg_areas", schema = "public")
public class SiggAreas {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long gid;

	@Column(name = "sgg_nm")
	private String sggName;

	@Column(name = "lat")
	private Double lat;

	@Column(name = "lon")
	private Double lon;

	@Column(name = "geohash")
	private String geohash;

	@Column(name = "adm_sect_c")
	private String admSectCode;

	@Column(name = "sgg_oid")
	private Integer sggOid;

	@Column(name = "col_adm_se")
	private String colAdmSe;

	@Column(columnDefinition = "geometry")
	private Geometry geom;

	@Column(columnDefinition = "geometry")
	private Geometry center;

	@Column(name = "level")
	private String level;

	@Column(name = "parent_id")
	private Integer parentId;


	@Builder
	public SiggAreas(Long gid, String sggName, Double lat, Double lon, String geohash) {
		this.gid = gid;
		this.sggName = sggName;
		this.lat = lat;
		this.lon = lon;
		this.geohash = geohash;
	}

}
