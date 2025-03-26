package com.example.log4u.domain.supports.repository;

import com.example.log4u.domain.supports.entity.Support;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportRepository extends JpaRepository<Support, Long> {
}
