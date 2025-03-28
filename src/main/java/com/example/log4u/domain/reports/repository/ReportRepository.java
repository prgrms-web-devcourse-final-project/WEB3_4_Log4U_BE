package com.example.log4u.domain.reports.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.log4u.domain.reports.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
