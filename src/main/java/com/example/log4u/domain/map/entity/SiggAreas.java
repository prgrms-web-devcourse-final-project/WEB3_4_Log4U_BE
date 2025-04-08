package com.example.log4u.domain.map.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Geometry;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "sigg_areas", schema = "public")
public class SiggAreas {

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
}
