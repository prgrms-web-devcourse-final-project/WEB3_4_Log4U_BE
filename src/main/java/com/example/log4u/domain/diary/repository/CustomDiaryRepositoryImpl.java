package com.example.log4u.domain.diary.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.example.log4u.common.util.PageableUtil;
import com.example.log4u.domain.diary.SortType;
import com.example.log4u.domain.diary.VisibilityType;
import com.example.log4u.domain.diary.dto.DiaryWithAuthorDto;
import com.example.log4u.domain.diary.entity.Diary;
import com.example.log4u.domain.diary.entity.QDiary;
import com.example.log4u.domain.hashtag.entity.QDiaryHashtag;
import com.example.log4u.domain.hashtag.entity.QHashtag;
import com.example.log4u.domain.like.entity.Like;
import com.example.log4u.domain.like.entity.QLike;
import com.example.log4u.domain.map.dto.response.DiaryMarkerResponseDto;
import com.example.log4u.domain.user.entity.QUser;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomDiaryRepositoryImpl implements CustomDiaryRepository {

	private final JPAQueryFactory queryFactory;

	private final QDiary diary = QDiary.diary;
	private final QLike like = QLike.like;
	private final QDiaryHashtag diaryHashtag = QDiaryHashtag.diaryHashtag;
	private final QHashtag hashtag = QHashtag.hashtag;
	private final QUser user = QUser.user;

	@Override
	public Slice<DiaryWithAuthorDto> findByUserIdAndVisibilityInAndCursorId(
		Long userId,
		List<VisibilityType> visibilities,
		Long cursorId,
		Pageable pageable
	) {
		// 조건 생성
		BooleanExpression condition = createSearchCondition(null, visibilities, userId);

		if (cursorId != null) {
			// 커서 다이어리 조회
			Diary cursorDiary = queryFactory
				.selectFrom(diary)
				.where(diary.diaryId.eq(cursorId))
				.fetchOne();

			if (cursorDiary != null) {
				// 생성 시간과 ID를 함께 고려
				condition = condition.and(
					diary.createdAt.lt(cursorDiary.getCreatedAt())
						.or(
							diary.createdAt.eq(cursorDiary.getCreatedAt())
								.and(diary.diaryId.lt(cursorId))
						)
				);
			} else {
				// 커서 다이어리를 찾을 수 없는 경우 기본 ID 기준 적용
				condition = condition.and(diary.diaryId.lt(cursorId));
			}
		}

		// 다이어리와 작성자 정보를 함께 조회
		List<DiaryWithAuthorDto> content = queryFactory
			.select(Projections.constructor(DiaryWithAuthorDto.class,
				diary,
				user.nickname,
				user.profileImage
			))
			.from(diary)
			.join(user).on(diary.userId.eq(user.userId))
			.where(condition)
			.orderBy(diary.createdAt.desc(), diary.diaryId.desc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		// 다음 페이지 여부를 계산하여 반환
		return PageableUtil.checkAndCreateSlice(content, pageable);
	}

	@Override
	public Slice<DiaryWithAuthorDto> searchDiariesByCursor(
		String keyword,
		List<VisibilityType> visibilities,
		SortType sort,
		Long cursorId,
		Pageable pageable
	) {
		// 기본 조건 생성(키워드 + 공개 범위)
		BooleanExpression condition = createSearchCondition(keyword, visibilities, null);

		// 커서 ID가 있으면 정렬 기준에 따라 조건 추가
		if (cursorId != null) {
			// 커서 다이어리 조회
			Diary cursorDiary = queryFactory
				.selectFrom(diary)
				.where(diary.diaryId.eq(cursorId))
				.fetchOne();

			if (cursorDiary != null) {
				// 정렬 기준에 따라 커서 조건 다르게 적용
				if (sort == SortType.POPULAR) {
					// 인기순 정렬일 때는 좋아요 수와 다이어리 ID를 함께 고려
					condition = condition.and(
						diary.likeCount.lt(cursorDiary.getLikeCount())
							.or(
								diary.likeCount.eq(cursorDiary.getLikeCount())
									.and(diary.diaryId.lt(cursorId))
							)
					);
				} else {
					// 최신순 정렬일 때는 생성 시간과 다이어리 ID를 함께 고려
					condition = condition.and(
						diary.createdAt.lt(cursorDiary.getCreatedAt())
							.or(
								diary.createdAt.eq(cursorDiary.getCreatedAt())
									.and(diary.diaryId.lt(cursorId))
							)
					);
				}
			} else {
				// 커서 다이어리를 찾을 수 없는 경우 기본 ID 기준 적용
				condition = condition.and(diary.diaryId.lt(cursorId));
			}
		}

		// 다이어리와 작성자 정보를 함께 조회
		List<DiaryWithAuthorDto> content = queryFactory
			.select(Projections.constructor(DiaryWithAuthorDto.class,
				diary,
				user.nickname,
				user.profileImage
			))
			.from(diary)
			.join(user).on(diary.userId.eq(user.userId))
			.where(condition)
			.orderBy(createOrderSpecifier(sort))
			.limit(pageable.getPageSize() + 1)
			.fetch();

		// PageableUtil 사용하여 Slice 생성
		return PageableUtil.checkAndCreateSlice(content, pageable);
	}

	// 하나의 메소드로 조건 생성
	private BooleanExpression createSearchCondition(
		String keyword,
		List<VisibilityType> visibilities,
		Long userId
	) {
		BooleanExpression condition = diary.visibility.in(visibilities)
			.and(userId != null ? diary.userId.eq(userId) : null);

		// keyword가 있을 경우 (제목, 내용, 해시태그 검색)
		if (StringUtils.hasText(keyword)) {
			// 제목 또는 내용에 키워드가 포함되는 경우
			BooleanExpression contentCondition = diary.title.containsIgnoreCase(keyword)
				.or(diary.content.containsIgnoreCase(keyword));

			// 해시태그에 키워드가 포함되는 경우
			BooleanExpression hashtagCondition = diary.diaryId.in(
				JPAExpressions
					.select(diaryHashtag.diaryId)
					.from(diaryHashtag)
					.join(hashtag).on(diaryHashtag.hashtagId.eq(hashtag.hashtagId))
					.where(hashtag.name.containsIgnoreCase(keyword))
			);

			// 제목/내용 조건과 해시태그 조건을 OR로 연결
			condition = condition.and(contentCondition.or(hashtagCondition));
		}

		return condition;
	}

	// 정렬 조건 생성
	private OrderSpecifier<?>[] createOrderSpecifier(SortType sort) {
		if (sort == null) {
			return new OrderSpecifier[] {diary.createdAt.desc(), diary.diaryId.desc()};
		}

		return switch (sort) {
			case POPULAR -> new OrderSpecifier[] {diary.likeCount.desc(), diary.diaryId.desc()};
			case LATEST -> new OrderSpecifier[] {diary.createdAt.desc(), diary.diaryId.desc()};
		};
	}

	@Override
	public Slice<Diary> getLikeDiarySliceByUserId(
		Long userId,
		List<VisibilityType> visibilities,
		Long cursorId,
		Pageable pageable
	) {
		// 기본 조건 생성
		BooleanExpression condition = createSearchCondition(null, visibilities, null);

		// 커서가 있는 경우, 커서 Like 객체를 조회
		if (cursorId != null) {
			Like cursorLike = queryFactory
				.selectFrom(like)
				.where(like.likeId.eq(cursorId))
				.fetchOne();

			if (cursorLike != null) {
				// createdAt + likeId 복합 커서 조건 추가
				condition = condition.and(
					like.createdAt.lt(cursorLike.getCreatedAt())
						.or(
							like.createdAt.eq(cursorLike.getCreatedAt())
								.and(like.likeId.lt(cursorId))
						)
				);
			}
		}

		// 다이어리 조회 (좋아요 기준으로)
		List<Diary> content = queryFactory
			.selectFrom(diary)
			.innerJoin(like)
			.on(like.diaryId.eq(diary.diaryId))
			.where(like.userId.eq(userId)
				.and(condition))
			.orderBy(like.createdAt.desc(), like.likeId.desc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		// 다음 페이지 존재 여부 판단 및 Slice 반환
		return PageableUtil.checkAndCreateSlice(content, pageable);
	}

	@Override
	public List<DiaryMarkerResponseDto> findDiariesInBounds(double south, double north, double west, double east) {

		return queryFactory
			.select(Projections.constructor(DiaryMarkerResponseDto.class,
				diary.diaryId,
				diary.title,
				diary.thumbnailUrl,
				diary.likeCount,
				diary.location.latitude,
				diary.location.longitude,
				diary.createdAt
			))
			.from(diary)
			.where(
				diary.visibility.eq(VisibilityType.PUBLIC),
				diary.location.latitude.between(south, north),
				diary.location.longitude.between(west, east)
			)
			.orderBy(diary.createdAt.asc())
			.fetch();
	}

	@Override
	public List<Diary> findInBoundsByUserId(Long userId, double south, double north, double west, double east) {

		return queryFactory
			.selectFrom(diary)
			.where(
				diary.userId.eq(userId),
				diary.location.latitude.between(south, north),
				diary.location.longitude.between(west, east)
			)
			.fetch();
	}

	@Override
	public List<DiaryMarkerResponseDto> findMyDiariesInBounds(Long userId, double south, double north, double west,
		double east) {
		return queryFactory
			.select(Projections.constructor(DiaryMarkerResponseDto.class,
				diary.diaryId,
				diary.title,
				diary.thumbnailUrl,
				diary.likeCount,
				diary.location.latitude,
				diary.location.longitude,
				diary.createdAt
			))
			.from(diary)
			.where(
				diary.userId.eq(userId),
				diary.location.latitude.between(south, north),
				diary.location.longitude.between(west, east)
			)
			.orderBy(diary.createdAt.asc())
			.fetch();
	}
}
