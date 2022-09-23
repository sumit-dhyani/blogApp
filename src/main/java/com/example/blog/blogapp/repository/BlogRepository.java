package com.example.blog.blogapp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.blog.blogapp.entity.Post;

@Repository
public interface BlogRepository extends JpaRepository<Post, Long> {

//	List<Post> findByTitleOrContentOrTagsOrAuthor(String keyword);

	Page<Post> findAll(Pageable paging);
}
