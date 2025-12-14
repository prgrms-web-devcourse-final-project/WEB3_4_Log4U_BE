package com.example.log4u.domain.map.repository.sigg.query;

import java.util.List;

import com.example.log4u.domain.map.dto.response.GetDiaryClusterResponse;

public interface SiggAreasRepositoryCustom {

	List<GetDiaryClusterResponse> findSiggAreasCluster(String geohashPrefix);

	List<GetDiaryClusterResponse> findSiggAreasClusterByIndexScan(String geohashPrefix);

}
