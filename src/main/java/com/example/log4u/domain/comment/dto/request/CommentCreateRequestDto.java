package com.example.log4u.domain.comment.dto.request;

import com.example.log4u.domain.comment.entity.Comment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentCreateRequestDto(

	@NotNull
	Long diaryId,

	@NotBlank
	@Size(max = 1000)
	String content
) {
	public Comment toEntity(Long userId) {
		return Comment.builder()
			.userId(userId)
			.diaryId(diaryId)
			.content(content)
			.build();
	}
}
