package com.example.log4u.domain.map.repository.sigg.query;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.map.dto.response.GetDiaryClusterResponse;
import com.example.log4u.domain.map.dto.response.QGetDiaryClusterResponse;
import com.example.log4u.domain.map.entity.QSiggAreas;
import com.example.log4u.domain.map.entity.QSiggAreasDiaryCount;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
public class SiggAreasRepositoryImpl implements SiggAreasRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public SiggAreasRepositoryImpl(@Qualifier("postgresJPAQueryFactory") JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public List<GetDiaryClusterResponse> findSiggAreasCluster(String geohashPrefix) {
		QSiggAreas s = QSiggAreas.siggAreas;
		QSiggAreasDiaryCount c = QSiggAreasDiaryCount.siggAreasDiaryCount;

		return queryFactory
			.select(new QGetDiaryClusterResponse(
				s.sggName,
				s.gid,
				s.lat,
				s.lon,
				c.diaryCount.coalesce(0L)
			))
			.from(s)
			.leftJoin(c).on(s.gid.eq(c.id))
			.where(s.geohash.startsWith(geohashPrefix))
			.fetch();
	}

	@Override
	public List<GetDiaryClusterResponse> findSiggAreasClusterByIndexScan(String geohashPrefix) {
		QSiggAreas s = QSiggAreas.siggAreas;
		QSiggAreasDiaryCount c = QSiggAreasDiaryCount.siggAreasDiaryCount;

		return queryFactory
			.select(new QGetDiaryClusterResponse(
				s.sggName,
				s.gid,
				s.lat,
				s.lon,
				c.diaryCount.coalesce(0L)
			))
			.from(s)
			.leftJoin(c).on(s.gid.eq(c.id))
			.where(s.geohash.startsWith(geohashPrefix))
			.fetch();
	}
}
