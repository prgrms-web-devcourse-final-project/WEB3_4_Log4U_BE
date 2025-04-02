package com.example.log4u.domain.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.example.log4u.domain.comment.entity.Comment;

public interface CommentRepositoryCustom {

	Slice<Comment> findByDiaryIdWithCursor(Long diaryId, Long cursorCommentId, Pageable pageable);
}
