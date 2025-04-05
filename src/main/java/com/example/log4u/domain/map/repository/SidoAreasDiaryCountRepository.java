package com.example.log4u.domain.map.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.map.entity.SidoAreasDiaryCount;

@Repository
public interface SidoAreasDiaryCountRepository extends JpaRepository<SidoAreasDiaryCount, Long> {
}
