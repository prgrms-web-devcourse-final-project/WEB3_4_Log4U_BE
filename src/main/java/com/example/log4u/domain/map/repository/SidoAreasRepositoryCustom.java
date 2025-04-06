package com.example.log4u.domain.map.repository;

import java.util.List;

import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;

public interface SidoAreasRepositoryCustom {
	List<DiaryClusterResponseDto> findSidoAreaClusters(double south, double north, double west, double east);
}
