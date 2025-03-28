package com.example.log4u.domain.supports.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.log4u.domain.supports.entity.Support;

public interface SupportRepository extends JpaRepository<Support, Long> {
}
