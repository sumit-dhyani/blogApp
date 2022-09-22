package com.example.blog.blogapp.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.blog.blogapp.entity.Comment;
import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.entity.Tag;
import com.example.blog.blogapp.repository.BlogRepository;
import com.example.blog.blogapp.repository.CommentRepository;
import com.example.blog.blogapp.repository.TagRepository;
import com.example.blog.blogapp.service.BlogService;

@Service
public class BlogServiceImpl implements BlogService {
	@Autowired
	BlogRepository repository;
	@Autowired
	TagRepository tagRepository;
	
	@Override
	public List<Post> getBlogPosts() {
		System.out.println(repository.findAll());
		return repository.findAll(Sort.by(Sort.Direction.DESC,"id"));
	}

	@Override
	public void createPost(Post newPost) {
		newPost.setAuthor("Anonymous");
		newPost.setCreatedAt(LocalDateTime.now());
		newPost.setIsPublished(false);
		if(newPost.getContent().length()>100) {
		newPost.setExcerpt(newPost.getContent().substring(0, 100));
		}
		else {
			newPost.setExcerpt(newPost.getContent());
		}
		List<String> tagList=Arrays.asList(newPost.getTagField().split(","));
		for(String tag:tagList) {
			Optional<Tag> existingTag=tagRepository.findByName(tag);
			if(existingTag.isPresent()) {
				newPost.getTags().add(existingTag.get());
			}
			else {
			Tag tags=new Tag(tag,LocalDateTime.now());
			tags.getPostTag().add(newPost);
			newPost.getTags().add(tags);
			}
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
