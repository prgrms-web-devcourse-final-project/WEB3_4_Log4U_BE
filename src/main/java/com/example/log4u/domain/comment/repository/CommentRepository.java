package com.example.log4u.domain.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.log4u.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
