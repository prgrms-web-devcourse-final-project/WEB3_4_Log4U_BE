package com.example.log4u.domain.map.repository.sigg.query;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.map.dto.response.DiaryClusterResponseDto;
import com.example.log4u.domain.map.dto.response.QDiaryClusterResponseDto;
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
	public List<DiaryClusterResponseDto> findSiggAreaClusters(double south, double north, double west, double east) {
		QSiggAreas s = QSiggAreas.siggAreas;
		QSiggAreasDiaryCount c = QSiggAreasDiaryCount.siggAreasDiaryCount;

		return queryFactory
			.select(new QDiaryClusterResponseDto(
				s.sggName,
				s.gid,
				s.lat,
				s.lon,
				c.diaryCount.coalesce(0L)
			))
			.from(s)
			.leftJoin(c).on(s.gid.eq(c.id))
			.where(
				s.lat.between(south, north),
				s.lon.between(west, east)
			)
			.fetch();
	}
}
