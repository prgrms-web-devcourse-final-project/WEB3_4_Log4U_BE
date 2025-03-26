package com.example.log4u.domain.reports.repository;

import com.example.log4u.domain.reports.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
