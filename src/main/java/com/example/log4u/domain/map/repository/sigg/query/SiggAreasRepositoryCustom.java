package com.example.log4u.domain.map.repository.sigg.query;

import java.util.List;

import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;

public interface SiggAreasRepositoryCustom {
	List<DiaryClusterResponseDto> findSiggAreaClusters(double south, double north, double west, double east);
}
