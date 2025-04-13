package com.example.log4u.domain.follow.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.example.log4u.common.util.PageableUtil;
import com.example.log4u.domain.follow.entitiy.Follow;
import com.example.log4u.domain.follow.entitiy.QFollow;
import com.example.log4u.domain.user.dto.UserThumbnailResponseDto;
import com.example.log4u.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberPath;

@Repository
public class FollowQuerydsl extends QuerydslRepositorySupport {
	private static final QFollow follow = QFollow.follow;
	private static final QUser user = QUser.user;

	public FollowQuerydsl() {
		super(Follow.class);
	}

	private NumberPath<Long> getNumberPath(boolean isFollowTarget) {
		return isFollowTarget ? follow.targetId : follow.initiatorId;
	}

	private BooleanBuilder getBooleanBuilder(boolean isFollowTarget, Long userId, Long cursorId) {
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(getNumberPath(isFollowTarget).eq(userId));

		if (cursorId != null) {
			builder.and(follow.id.lt(cursorId));
		}

		return builder;
	}

	private List<UserThumbnailResponseDto> getContent(boolean isFollowTarget, Long userId, Long cursorId) {
		BooleanBuilder builder = getBooleanBuilder(isFollowTarget, userId, cursorId);

		NumberPath<Long> numberPath = getNumberPath(isFollowTarget);

		return from(follow)
			.innerJoin(user)
			.on(user.userId.eq(numberPath))
			.select(Projections.constructor(UserThumbnailResponseDto.class,
				numberPath,
				user.nickname,
				user.profileImage))
			.where(builder)
			.distinct()
			.fetch();
	}

	//내 팔로워 아이디 슬라이스
	public Slice<UserThumbnailResponseDto> getFollowerSliceByUserId(Long userId, Long cursorId, Pageable pageable) {
		boolean isFollowTarget = true;
		List<UserThumbnailResponseDto> content = getContent(isFollowTarget, userId, cursorId);
		return PageableUtil.checkAndCreateSlice(content, pageable);
	}

	// 내가 팔로잉하는 아이디 슬라이스
	public Slice<UserThumbnailResponseDto> getFollowingSliceByUserId(Long userId, Long cursorId, Pageable pageable) {
		boolean isFollowTarget = false;
		List<UserThumbnailResponseDto> content = getContent(isFollowTarget, userId, cursorId);
		return PageableUtil.checkAndCreateSlice(content, pageable);
	}
}