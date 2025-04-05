package com.example.log4u.domain.map.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.map.dto.response.AreaClusterProjection;
import com.example.log4u.domain.map.entity.SiggAreas;

@Repository
public interface SiggAreasRepository extends JpaRepository<SiggAreas, Long> {

	@Query(value = """
			SELECT s.gid AS id,
			       s.sgg_nm AS name,
			       s.lat AS lat,
			       s.lon AS lon,
			       COALESCE(c.diary_count, 0) AS diaryCount
			FROM sigg_areas s
			LEFT JOIN sigg_areas_diary_count c ON s.gid = c.id
			WHERE s.lat BETWEEN :south AND :north
			  AND s.lon BETWEEN :west AND :east
		""", nativeQuery = true)
	List<AreaClusterProjection> findSiggAreaClusters(
		@Param("south") double south,
		@Param("north") double north,
		@Param("west") double west,
		@Param("east") double east
	);

	@Query("""
		    SELECT r FROM SiggAreas r
		    WHERE ST_Contains(r.geom, ST_SetSRID(ST_Point(:lon, :lat), 4326)) = true
		""")
	Optional<SiggAreas> findRegionByLatLon(@Param("lat") Double lat, @Param("lon") Double lon);
}
