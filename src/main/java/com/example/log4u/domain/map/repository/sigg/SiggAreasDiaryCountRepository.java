package com.example.log4u.domain.map.repository.sigg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.map.entity.SiggAreasDiaryCount;

@Repository
public interface SiggAreasDiaryCountRepository extends JpaRepository<SiggAreasDiaryCount, Long> {
}