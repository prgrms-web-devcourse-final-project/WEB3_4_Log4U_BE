package com.example.log4u.domain.map.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.map.entity.SiggAreas;

@Repository
public interface SiggAreasRepository extends JpaRepository<SiggAreas, Long>, SiggAreasRepositoryCustom {

	@Query("""
		    SELECT r FROM SiggAreas r
		    WHERE ST_Contains(r.geom, ST_SetSRID(ST_Point(:lon, :lat), 4326)) = true
		""")
	Optional<SiggAreas> findRegionByLatLon(@Param("lat") Double lat, @Param("lon") Double lon);
}
