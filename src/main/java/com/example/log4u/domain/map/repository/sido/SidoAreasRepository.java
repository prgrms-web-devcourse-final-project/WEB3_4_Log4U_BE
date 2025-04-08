package com.example.log4u.domain.map.repository.sido;

import java.util.List;
import java.util.Optional;

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
	Optional<SidoAreas> findRegionByLatLon(@Param("lat") Double lat, @Param("lon") Double lon);


	@Query("""
		SELECT s FROM SidoAreas s
		WHERE s.lat BETWEEN :south AND :north
		  AND s.lon BETWEEN :west AND :east
	""")
	List<SidoAreas> findWithinBoundingBox(@Param("south") double south, @Param("north") double north, @Param("west") double west, @Param("east") double east
	);
}
