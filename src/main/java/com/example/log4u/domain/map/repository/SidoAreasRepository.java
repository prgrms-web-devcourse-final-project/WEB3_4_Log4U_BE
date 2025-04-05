package com.example.log4u.domain.map.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.map.dto.response.AreaClusterProjection;
import com.example.log4u.domain.map.entity.SidoAreas;

@Repository
public interface SidoAreasRepository extends JpaRepository<SidoAreas, Long> {

	@Query(value = """
			SELECT s.id AS id,
			       s.name AS name,
			       s.lat AS lat,
			       s.lon AS lon,
			       COALESCE(c.diary_count, 0) AS diaryCount
			FROM sido_areas s
			LEFT JOIN sido_areas_diary_count c ON s.id = c.id
			WHERE s.lat BETWEEN :south AND :north
			  AND s.lon BETWEEN :west AND :east
		""", nativeQuery = true)
	List<AreaClusterProjection> findSidoAreaClusters(
		@Param("south") double south,
		@Param("north") double north,
		@Param("west") double west,
		@Param("east") double east
	);

	@Query("""
		    SELECT s FROM SidoAreas s
		    WHERE ST_Contains(s.geom, ST_SetSRID(ST_Point(:lon, :lat), 4326)) = true
		""")
	Optional<SidoAreas> findRegionByLatLon(@Param("lat") Double lat, @Param("lon") Double lon);

}
