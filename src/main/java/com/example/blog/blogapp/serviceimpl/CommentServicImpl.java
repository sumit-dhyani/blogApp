package com.example.blog.blogapp.serviceimpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.blog.blogapp.entity.Comment;
import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.repository.CommentRepository;
import com.example.blog.blogapp.service.CommentService;
@Service
public class CommentServicImpl implements CommentService {
	@Autowired
	BlogServiceImpl service;
	@Autowired
	CommentRepository commentRepo;
	@Override
	public void createComment(Comment comment, long id) {
			
			Post existingPost=service.returnBlog((Long)id);
			comment.setCreatedAt(LocalDateTime.now());
			comment.setPost(existingPost);
			commentRepo.save(comment);

	}
	
	public String deleteComment(long id) {
		Comment commentToBeDeleted=commentRepo.findById(id).orElseThrow(()->new RuntimeException("Comment not found"));
		Long postId=commentToBeDeleted.getPost().getId();
		commentRepo.deleteById(id);
		return postId.toString();
	}

}
