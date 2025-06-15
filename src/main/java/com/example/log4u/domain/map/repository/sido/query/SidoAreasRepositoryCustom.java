package com.example.log4u.domain.map.repository.sido.query;

import java.util.List;

import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;

public interface SidoAreasRepositoryCustom {

	List<DiaryClusterResponseDto> findByGeohashPrefix(String geohashPrefix);
}
