package com.example.log4u.fixture;

import com.example.log4u.domain.comment.entity.Comment;

public class CommentFixture {

	public static Comment createCommentFixture(Long commentId, Long userId, Long diaryId) {
		return Comment.builder()
			.commentId(commentId)
			.userId(userId)
			.diaryId(diaryId)
			.content("댓글 내용")
			.build();
	}

	public static Comment createDefaultComment() {
		return createCommentFixture(1L, 1L, 1L);
	}
}
