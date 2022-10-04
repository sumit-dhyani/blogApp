package com.example.blog.blogapp.service;

import com.example.blog.blogapp.entity.Comment;
import org.springframework.security.core.Authentication;

public interface CommentService {
	public void createComment(Comment comment, long id, Authentication authentication);

	public String deleteComment(long id);
}