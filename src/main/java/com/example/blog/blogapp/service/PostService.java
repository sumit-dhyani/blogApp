package com.example.blog.blogapp.service;

import java.util.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.blog.blogapp.entity.Post;
import org.springframework.security.core.Authentication;

public interface PostService {

	public void createPost(Post newPost,Authentication authentication);

	public void updatePost(Post newPost);

	public boolean deletePost(Long id, Authentication authentication);

	public Post returnBlog(Long id);

	public Page<Post> paginatedPosts(Pageable pagination);

}
