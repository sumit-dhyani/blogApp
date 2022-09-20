package com.example.blog.blogapp.serviceimpl;

import java.time.LocalDateTime;

import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.repository.BlogRepository;
import com.example.blog.blogapp.service.BlogService;

@Service
public class BlogServiceImpl implements BlogService {
	@Autowired
	BlogRepository repository;
	
	@Override
	public List<Post> getBlogPosts() {
		System.out.println(repository.findAll());
		return repository.findAll();
	}

	@Override
	public void createPost(Post newPost) {
		newPost.setAuthor("Anonymous");
		newPost.setCreatedAt(LocalDateTime.now());
		newPost.setIsPublished(false);
		
		newPost.setExcerpt(newPost.getContent().substring(0, 20));
		repository.save(newPost);
		
	}
	
	public void updatePost(Post updatedPost) {
		Post existingPost=repository.findById(updatedPost.getId())
				.orElseThrow(()->new RuntimeException("not found"));
		existingPost=updatedPost;
		repository.save(existingPost);
		
		
	}
	
	public Post returnBlog(Long id) {
		return repository.findById(id).orElseThrow(()->new RuntimeException("Blog not present"));
	}
	
	public BlogServiceImpl() {
	}
	

}
