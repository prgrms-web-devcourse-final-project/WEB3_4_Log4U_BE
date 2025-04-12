package com.example.log4u.domain.comment.repository;

import static com.example.log4u.domain.comment.entity.QComment.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.comment.dto.response.CommentResponseDto;
import com.example.log4u.domain.comment.entity.Comment;
import com.example.log4u.domain.comment.entity.QComment;
import com.example.log4u.domain.user.entity.QUser;
import com.example.log4u.domain.user.entity.User;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Slice<CommentResponseDto> findWithUserByDiaryId(Long diaryId, Long cursorCommentId, Pageable pageable) {
		QComment comment = QComment.comment;
		QUser user = QUser.user;

		List<Tuple> tuples = queryFactory
			.select(comment, user)
			.from(comment)
			.join(user).on(comment.userId.eq(user.userId))
			.where(
				comment.diaryId.eq(diaryId),
				cursorCommentId != null ? comment.commentId.lt(cursorCommentId) : null
			)
			.orderBy(comment.commentId.desc())
			.limit(pageable.getPageSize() + 1)
			.fetch();

		boolean hasNext = tuples.size() > pageable.getPageSize();
		List<CommentResponseDto> content = tuples.stream()
			.limit(pageable.getPageSize())
			.map(tuple -> {
				Comment c = tuple.get(comment);
				User u = tuple.get(user);
				return CommentResponseDto.of(c, u);
			})
			.toList();

		return new SliceImpl<>(content, pageable, hasNext);
	}
}
