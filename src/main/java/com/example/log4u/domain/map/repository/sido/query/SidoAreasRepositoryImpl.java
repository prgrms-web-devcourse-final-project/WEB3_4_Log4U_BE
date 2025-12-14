package com.example.log4u.domain.map.repository.sido.query;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.map.dto.response.GetDiaryClusterResponse;
import com.example.log4u.domain.map.dto.response.QGetDiaryClusterResponse;
import com.example.log4u.domain.map.entity.QSidoAreas;
import com.example.log4u.domain.map.entity.QSidoAreasDiaryCount;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class SidoAreasRepositoryImpl implements SidoAreasRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public SidoAreasRepositoryImpl(@Qualifier("postgresJPAQueryFactory") JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public List<GetDiaryClusterResponse> findSidoAreasCluster(String geohashPrefix) {
		QSidoAreas s = QSidoAreas.sidoAreas;
		QSidoAreasDiaryCount c = QSidoAreasDiaryCount.sidoAreasDiaryCount;

		return queryFactory
			.select(new QGetDiaryClusterResponse(
				s.name,
				s.id,
				s.lat,
				s.lon,
				c.diaryCount.coalesce(0L)
			))
			.from(s)
			.leftJoin(c).on(s.id.eq(c.id))
			.where(s.geohash.startsWith(geohashPrefix))
			.fetch();
	}

	@Override
	public List<GetDiaryClusterResponse> findSidoAreasClusterByIndexScan(String geohashPrefix) {
		QSidoAreas s = QSidoAreas.sidoAreas;
		QSidoAreasDiaryCount c = QSidoAreasDiaryCount.sidoAreasDiaryCount;

		return queryFactory
			.select(new QGetDiaryClusterResponse(
				s.name,
				s.id,
				s.lat,
				s.lon,
				c.diaryCount.coalesce(0L)
			))
			.from(s)
			.leftJoin(c).on(s.id.eq(c.id))
			.where(s.geohash.startsWith(geohashPrefix))
			.fetch();
	}
}
