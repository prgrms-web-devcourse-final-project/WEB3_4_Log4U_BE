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

}
