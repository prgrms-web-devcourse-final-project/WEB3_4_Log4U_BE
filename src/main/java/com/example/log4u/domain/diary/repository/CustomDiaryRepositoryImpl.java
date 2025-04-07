package com.example.log4u.domain.diary.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.example.log4u.domain.diary.SortType;
import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.entity.QDiary;
import com.example.log4u.domain.map.dto.response.DiaryMarkerResponseDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomDiaryRepositoryImpl implements CustomDiaryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Diary> searchDiaries(
		String keyword,
		List<VisibilityType> visibilities,
		SortType sort,
		Pageable pageable
	) {
		QDiary diary = QDiary.diary;

		// 조건 생성
		BooleanExpression condition = createCondition(diary, keyword, visibilities, null);

		// 쿼리 실행
		JPAQuery<Diary> query = queryFactory
			.selectFrom(diary)
			.where(condition);

		// 전체 카운트 조회
		Long total = queryFactory
			.select(diary.count())
			.from(diary)
			.where(condition)
			.fetchOne();

		// 데이터 조회
		List<Diary> content = query
			.orderBy(createOrderSpecifier(diary, sort))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		return new PageImpl<>(content, pageable, total != null ? total : 0);
	}

	@Override
	public Slice<Diary> findByUserIdAndVisibilityInAndCursorId(
		Long userId,
		List<VisibilityType> visibilities,
		Long cursorId,
		Pageable pageable
	) {
		QDiary diary = QDiary.diary;

		// 조건 생성
		BooleanExpression condition = createCondition(diary, null, visibilities, userId);

		if (cursorId != null) {
			condition = condition.and(diary.diaryId.lt(cursorId)); // 커서 ID보다 작은 ID만 조회
		}

		// limit + 1로 다음 페이지 존재 여부 확인
		List<Diary> content = queryFactory
			.selectFrom(diary)
			.where(condition)
			.orderBy(diary.diaryId.desc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		// 다음 페이지 여부를 계산하여 반환
		return checkAndCreateSlice(content, pageable);
	}

	// 하나의 메소드로 조건 생성
	private BooleanExpression createCondition(
		QDiary diary,
		String keyword,
		List<VisibilityType> visibilities,
		Long userId
	) {
		BooleanExpression condition = diary.visibility.in(visibilities);

		// keyword가 있을 경우
		if (StringUtils.hasText(keyword)) {
			condition = condition.and(diary.title.containsIgnoreCase(keyword)
				.or(diary.content.containsIgnoreCase(keyword)));
		}

		// userId가 있을 경우
		if (userId != null) {
			condition = condition.and(diary.userId.eq(userId));
		}

		return condition;
	}

	// 정렬 조건 생성
	private OrderSpecifier<?> createOrderSpecifier(QDiary diary, SortType sort) {
		if (sort == null) {
			return diary.createdAt.desc();
		}

		return switch (sort) {
			case POPULAR -> diary.likeCount.desc();
			case LATEST -> diary.createdAt.desc();
		};
	}

	// Slice 생성 및 hasNext 처리
	private Slice<Diary> checkAndCreateSlice(List<Diary> content, Pageable pageable) {
		boolean hasNext = content.size() > pageable.getPageSize();

		// 다음 페이지가 있으면 마지막 항목 제거
		if (hasNext) {
			content.remove(content.size() - 1);  // removeLast() 대신 인덱스로 처리
		}

		return new SliceImpl<>(content, pageable, hasNext);
	}

	@Override
	public List<DiaryMarkerResponseDto> findDiariesInBounds(double south, double north, double west, double east) {
		QDiary d = QDiary.diary;

		return queryFactory
			.select(Projections.constructor(DiaryMarkerResponseDto.class,
				d.diaryId,
				d.title,
				d.thumbnailUrl,
				d.likeCount,
				d.latitude,
				d.longitude,
				d.createdAt
			))
			.from(d)
			.where(
				d.visibility.eq(VisibilityType.PUBLIC),
				d.latitude.between(south, north),
				d.longitude.between(west, east)
			)
			.orderBy(d.createdAt.asc())
			.fetch();
	}

	@Override
	public List<Diary> findInBoundsByUserId(Long userId, double south, double north, double west, double east) {
		QDiary d = QDiary.diary;

		return queryFactory
			.selectFrom(d)
			.where(
				d.userId.eq(userId),
				d.latitude.between(south, north),
				d.longitude.between(west, east)
			)
			.fetch();
	}

	@Override
	public List<DiaryMarkerResponseDto> findMyDiariesInBounds(Long userId, double south, double north, double west, double east) {
		QDiary d = QDiary.diary;

		return queryFactory
			.select(Projections.constructor(DiaryMarkerResponseDto.class,
				d.diaryId,
				d.title,
				d.thumbnailUrl,
				d.likeCount,
				d.latitude,
				d.longitude,
				d.createdAt
			))
			.from(d)
			.where(
				d.userId.eq(userId),
				d.latitude.between(south, north),
				d.longitude.between(west, east)
			)
			.orderBy(d.createdAt.asc())
			.fetch();
	}

}
