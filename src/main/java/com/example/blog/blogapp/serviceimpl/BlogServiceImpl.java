package com.example.blog.blogapp.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.time.LocalDateTime;

import java.util.List;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.example.blog.blogapp.entity.Comment;
import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.entity.Tag;
import com.example.blog.blogapp.repository.BlogRepository;
import com.example.blog.blogapp.repository.CommentRepository;
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
		newPost.setExcerpt(newPost.getContent().substring(0, 100));
		List<String> tagList=Arrays.asList(newPost.getTagField().split(","));
		for(String s:tagList) {
			Tag tags=new Tag(s,LocalDateTime.now());
			tags.getPostTag().add(newPost);
			newPost.getTags().add(tags);
		}
		
		repository.save(newPost);
		
	}
	
	public void updatePost(Post newPost) {
		Post postToUpdate=returnBlog(newPost.getId());
		postToUpdate.setContent(newPost.getContent());
		postToUpdate.setTitle(newPost.getTitle());
		postToUpdate.setUpdatedAt(LocalDateTime.now());
		
		repository.save(postToUpdate);
	}
	
	public void deletePost(Long id) {
		repository.deleteById(id);
		
	}
//	
	public Post returnBlog(Long id) {
		return repository.findById(id).
				orElseThrow(()->new RuntimeException("Blog not present"));
	}
	
	
	public BlogServiceImpl() {
	}
	

}
