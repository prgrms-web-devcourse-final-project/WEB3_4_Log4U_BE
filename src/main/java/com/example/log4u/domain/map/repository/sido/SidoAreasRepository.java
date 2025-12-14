package com.example.log4u.domain.map.repository.sido;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.map.entity.SidoAreas;
import com.example.log4u.domain.map.repository.sido.query.SidoAreasRepositoryCustom;

@Repository
public interface SidoAreasRepository extends JpaRepository<SidoAreas, Long>, SidoAreasRepositoryCustom {

	@Query("""
		SELECT s FROM SidoAreas s
		WHERE ST_Contains(s.geom, ST_SetSRID(ST_Point(:lon, :lat), 4326)) = true
		""")
	SidoAreas findSidoAreasByLatLon(@Param("lat") Double lat, @Param("lon") Double lon);

	@Query("""
		SELECT CASE WHEN COUNT(sOld) > 0 THEN true ELSE false END
		FROM SidoAreas sOld, SidoAreas sNew
		WHERE ST_Contains(sOld.geom, ST_SetSRID(ST_Point(:oldLon, :oldLat), 4326)) = true
		  AND ST_Contains(sNew.geom, ST_SetSRID(ST_Point(:newLon, :newLat), 4326)) = true
		  AND sOld.id = sNew.id
		""")
	boolean isSameSidoRegion(@Param("oldLat") Double oldLat, @Param("oldLon") Double oldLon, @Param("newLat") Double newLat, @Param("newLon") Double newLon);
}
