package com.example.log4u.fixture;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.example.log4u.domain.comment.dto.response.CommentResponseDto;
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

	public static List<Comment> createCommentsListFixture(int count) {
		List<Comment> comments = new ArrayList<>();
		Long diaryId = 1L;

		for (int i = 1; i <= count; i++) {
			comments.add(Comment.builder()
				.commentId((long) i)
				.userId((long) i)
				.diaryId(diaryId)
				.content("댓글" + i)
				.build());
		}
		return comments;
	}

	public static List<CommentResponseDto> createCommentDtos(int size) {
		return IntStream.rangeClosed(1, size)
			.mapToObj(i -> new CommentResponseDto(
				(long) i,
				(long) i,
				"사용자" + i,
				"https://cdn.example.com/user" + i + ".png",
				"댓글 " + i,
				LocalDateTime.now().minusMinutes(i) // createdAt
			)).toList();
	}
}
