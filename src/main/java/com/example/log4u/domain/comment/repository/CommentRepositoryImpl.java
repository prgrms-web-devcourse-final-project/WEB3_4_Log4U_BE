package com.example.log4u.domain.comment.repository;

import static com.example.log4u.domain.comment.entity.QComment.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.example.log4u.domain.comment.entity.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Slice<Comment> findByDiaryIdWithCursor(Long diaryId, Long cursorCommentId, Pageable pageable) {
		List<Comment> result = queryFactory
			.selectFrom(comment)
			.where(
				comment.diaryId.eq(diaryId),
				cursorCommentId != null ? comment.commentId.lt(cursorCommentId) : null
			)
			.orderBy(comment.commentId.desc())
			.limit(pageable.getPageSize() + 1) // 커서 기반 페이징
			.fetch();

		boolean hasNext = result.size() > pageable.getPageSize();
		List<Comment> content = hasNext ? result.subList(0, pageable.getPageSize()) : result;

		return new SliceImpl<>(content, pageable, hasNext);
	}
}
