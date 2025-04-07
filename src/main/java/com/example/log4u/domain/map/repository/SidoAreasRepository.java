package com.example.log4u.domain.map.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.map.entity.SidoAreas;

@Repository
public interface SidoAreasRepository extends JpaRepository<SidoAreas, Long>,SidoAreasRepositoryCustom {

	@Query("""
		    SELECT s FROM SidoAreas s
		    WHERE ST_Contains(s.geom, ST_SetSRID(ST_Point(:lon, :lat), 4326)) = true
		""")
	Optional<SidoAreas> findRegionByLatLon(@Param("lat") Double lat, @Param("lon") Double lon);
}
