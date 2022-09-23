package com.example.blog.blogapp.service;
import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.blog.blogapp.entity.Post;
public interface BlogService {
	public List<Post> getBlogPosts();
	public void createPost(Post newPost);
	public void updatePost(Post newPost);
	public void deletePost(Long id);
	public Post returnBlog(Long id);
	public Page<Post> paginatedPosts(Pageable pagination);
}
