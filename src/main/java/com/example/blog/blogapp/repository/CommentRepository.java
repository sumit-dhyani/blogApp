package com.example.blog.blogapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.blog.blogapp.entity.Comment;

@Repository
public interface CommentRepository  extends JpaRepository<Comment, Long>{

}
