package com.example.log4u.domain.supports.repository;

import com.example.log4u.domain.supports.dto.SupportGetResponseDto;
import com.example.log4u.domain.supports.dto.SupportOverviewGetResponseDto;
import com.example.log4u.domain.supports.entity.QSupport;
import com.example.log4u.domain.supports.entity.Support;
import com.example.log4u.domain.supports.supportType.SupportType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SupportQuerydsl extends QuerydslRepositorySupport {
    private final QSupport support = QSupport.support;

    public SupportQuerydsl() {
        super(Support.class);
    }

    public Page<SupportOverviewGetResponseDto> getSupportOverviewGetResponseDtoPage(
            Pageable pageable,
            SupportType supportType) {
        BooleanBuilder builder = new BooleanBuilder();

        if (supportType != null){
            builder.and(support.supportType.eq(supportType));
        }

        List<SupportOverviewGetResponseDto> content = from(support)
                .select(Projections.constructor(SupportOverviewGetResponseDto.class,
                        support.id,
                        support.supportType,
                        support.title,
                        support.createdAt,
                        support.answeredAt.isNotNull() // answered 필드는 answeredAt이 null 이 아니면 true
                ))
                .where(builder)
                .orderBy(support.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, () -> from(support)
                .fetchCount());
    }

    public SupportGetResponseDto getSupportGetResponseDtoById(Long supportId) {
        return from(support)
                .select(Projections.constructor(SupportGetResponseDto.class,
                        support.id,
                        support.supportType,
                        support.title,
                        support.content,
                        support.createdAt,
                        support.answerContent,
                        support.answeredAt))
                .where(support.id.eq(supportId))
                .fetchOne();
    }
}
