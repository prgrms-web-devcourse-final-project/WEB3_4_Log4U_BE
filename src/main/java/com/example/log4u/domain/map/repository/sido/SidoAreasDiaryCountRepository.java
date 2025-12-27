package com.example.log4u.domain.map.repository.sido;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.map.entity.SidoAreasDiaryCount;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface SidoAreasDiaryCountRepository extends JpaRepository<SidoAreasDiaryCount, Long> {

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
		    UPDATE SidoAreasDiaryCount c
		    SET c.diaryCount = c.diaryCount + 1
		    WHERE c.id = :id
		""")
	void increase(@Param("id") Long id);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
		    UPDATE SidoAreasDiaryCount c
		    SET c.diaryCount = c.diaryCount - 1
		    WHERE c.id = :id
		      AND c.diaryCount > 0
		""")
	void decrease(@Param("id") Long id);

}
