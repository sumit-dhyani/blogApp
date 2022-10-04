package com.example.blog.blogapp.service;

import com.example.blog.blogapp.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostDataService {
    Page<Post> filteredPosts(String[] authorIds, String[] tagIds, String searchField, Pageable pagination);
}
