package com.example.blog.blogapp.service;

import java.util.List;
import java.util.Optional;

import com.example.blog.blogapp.entity.Tag;

public interface TagService {
	
	public Tag getTagById(Long id);
	
	public List<Tag> getLinkedTags();

}
