package com.example.log4u.domain.map.repository.sigg;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.map.entity.SiggAreas;
import com.example.log4u.domain.map.repository.sigg.query.SiggAreasRepositoryCustom;

@Repository
public interface SiggAreasRepository extends JpaRepository<SiggAreas, Long>, SiggAreasRepositoryCustom {

	@Query("""
		SELECT r FROM SiggAreas r
		WHERE ST_Contains(r.geom, ST_SetSRID(ST_Point(:lon, :lat), 4326)) = true
		""")
	SiggAreas findSiggAreasByLatLon(@Param("lat") Double lat, @Param("lon") Double lon);


	@Query("""
		SELECT CASE WHEN COUNT(r1) > 0 THEN true ELSE false END
		FROM SiggAreas r1, SiggAreas r2
		WHERE ST_Contains(r1.geom, ST_SetSRID(ST_Point(:oldLon, :oldLat), 4326)) = true
		  AND ST_Contains(r2.geom, ST_SetSRID(ST_Point(:newLon, :newLat), 4326)) = true
		  AND r1.gid = r2.gid
		""")
	boolean isSameSiggRegion(@Param("oldLat") Double oldLat,
		@Param("oldLon") Double oldLon,
		@Param("newLat") Double newLat,
		@Param("newLon") Double newLon);
}
