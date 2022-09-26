package com.example.blog.blogapp.service;

import com.example.blog.blogapp.entity.Comment;

public interface CommentService {
	public void createComment(Comment comment, long id);

	public String deleteComment(long id);
}