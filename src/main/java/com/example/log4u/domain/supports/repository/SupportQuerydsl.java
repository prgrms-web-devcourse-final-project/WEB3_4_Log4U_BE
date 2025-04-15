package com.example.log4u.domain.supports.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.supports.dto.SupportGetResponseDto;
import com.example.log4u.domain.supports.dto.SupportOverviewGetResponseDto;
import com.example.log4u.domain.supports.entity.QSupport;
import com.example.log4u.domain.supports.entity.Support;
import com.example.log4u.domain.supports.supportType.SupportType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;

@Repository
public class SupportQuerydsl extends QuerydslRepositorySupport {
	private final QSupport support = QSupport.support;

	public SupportQuerydsl() {
		super(Support.class);
	}

	public Page<SupportOverviewGetResponseDto> getSupportOverviewGetResponseDtoPage(
		long requesterId,
		Pageable pageable,
		SupportType supportType) {

		BooleanBuilder builder = new BooleanBuilder();
		builder.and(support.requesterId.eq(requesterId));

		if (supportType != null) {
			builder.and(support.supportType.eq(supportType));
		}

		List<SupportOverviewGetResponseDto> content = from(support)
			.select(Projections.constructor(SupportOverviewGetResponseDto.class,
				support.id,
				support.requesterId,
				support.supportType,
				support.title,
				support.createdAt,
				Expressions.booleanTemplate("case when {0} is not null then true else false end", support.answerContent)
			))
			.where(builder)
			.orderBy(support.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		return PageableExecutionUtils.getPage(content, pageable, () -> from(support)
			.fetchCount());
	}

	public SupportGetResponseDto getSupportGetResponseDtoById(
		long requesterId,
		Long supportId) {
		return from(support)
			.select(Projections.constructor(SupportGetResponseDto.class,
				support.id,
				support.requesterId,
				support.supportType,
				support.title,
				support.content,
				support.createdAt,
				support.answerContent,
				support.updatedAt))
			.where(support.id.eq(supportId)
				.and(support.requesterId.eq(requesterId)))
			.fetchOne();
	}
}
