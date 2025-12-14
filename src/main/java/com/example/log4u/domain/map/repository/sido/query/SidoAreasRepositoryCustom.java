package com.example.log4u.domain.map.repository.sido.query;

import java.util.List;

import com.example.log4u.domain.map.dto.response.GetDiaryClusterResponse;

public interface SidoAreasRepositoryCustom {

	List<GetDiaryClusterResponse> findSidoAreasCluster(String geohashPrefix);

	List<GetDiaryClusterResponse> findSidoAreasClusterByIndexScan(String geohashPrefix);

}
