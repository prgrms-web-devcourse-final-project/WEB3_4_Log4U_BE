package com.example.log4u.domain.map.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.map.entity.SiggAreasDiaryCount;

@Repository
public interface SiggAreasDiaryCountRepository extends JpaRepository<SiggAreasDiaryCount, Long> {
}