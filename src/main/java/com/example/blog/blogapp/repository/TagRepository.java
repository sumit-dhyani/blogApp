package com.example.blog.blogapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.blog.blogapp.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
	
	Optional<Tag> findByName(String name);
}
