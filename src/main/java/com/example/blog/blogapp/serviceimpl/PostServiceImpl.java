package com.example.blog.blogapp.serviceimpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.example.blog.blogapp.entity.User;
import com.example.blog.blogapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.entity.Tag;
import com.example.blog.blogapp.repository.PostRepository;
import com.example.blog.blogapp.repository.TagRepository;
import com.example.blog.blogapp.service.PostService;

@Service
public class PostServiceImpl implements PostService {
	PostRepository postRepo;
	TagRepository tagRepo;
	UserRepository userRepo;

	@Autowired
	public PostServiceImpl(PostRepository postRepo,TagRepository tagRepo,UserRepository userRepo){
		this.postRepo=postRepo;
		this.tagRepo=tagRepo;
		this.userRepo=userRepo;
	}

	@Override
	public List<Post> getBlogPosts() {
		System.out.println(postRepo.findAll());
		return postRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	@Override
	public void createPost(Post newPost,Authentication authentication) {
		User user=userRepo.findByEmail(authentication.getName())
				.orElseThrow(()->new RuntimeException("user not found"));
		newPost.setAuthor(user.getName());
		newPost.setCreatedAt(LocalDateTime.now());
		newPost.setIsPublished(false);
		if (newPost.getContent().length() > 100) {
			newPost.setExcerpt(newPost.getContent().substring(0, 100));
		} else {
			newPost.setExcerpt(newPost.getContent());
		}
		newPost.setUser(user);
		addTags(newPost, newPost);
		postRepo.save(newPost);

	}

	public void updatePost(Post newPost) {
		Post postToUpdate = returnBlog(newPost.getId());
		postToUpdate.setContent(newPost.getContent());
		postToUpdate.setTitle(newPost.getTitle());
		postToUpdate.setUpdatedAt(LocalDateTime.now());
		postToUpdate.setTags(new ArrayList<>());
		if (newPost.getIsPublished()) {
			postToUpdate.setIsPublished(true);
		}
		postToUpdate.setPublishedAt(newPost.getPublishedAt());
		addTags(newPost, postToUpdate);

		if (newPost.getContent().length() > 100) {
			postToUpdate.setExcerpt(newPost.getContent().substring(0, 100));
		} else {
			postToUpdate.setExcerpt(newPost.getContent());
		}

		postToUpdate.setTagField(newPost.getTagField());
		postRepo.save(postToUpdate);
	}

	private void addTags(Post newPost, Post postToUpdate) {
		String[] tagList = newPost.getTagField().split(",");
		for (String tag : tagList) {
			Optional<Tag> existingTag = tagRepo.findByNameIgnoreCase(tag);
			if (existingTag.isPresent()) {
				postToUpdate.getTags().add(existingTag.get());
			} else {
				Tag tags = new Tag(tag, LocalDateTime.now());
				tags.getPostTag().add(postToUpdate);
				postToUpdate.getTags().add(tags);
			}
		}
	}

	public boolean deletePost(Long id,Authentication authentication) {
		Post post = returnBlog(id);
		if(authentication.getName().equals(post.getUser().getEmail())|
				authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
			post.setTags(new ArrayList<>());
			postRepo.deleteById(id);
			return true;
		}
		return false;

	}

//	
	public Post returnBlog(Long id) {
		return postRepo.findById(id).orElseThrow(() -> new RuntimeException("Blog not present"));
	}

	public Post returnBlog(Long id, Authentication authentication){
		Post post=postRepo.findById(id).orElseThrow(() -> new RuntimeException("Blog not present"));
		System.out.println("hello"+post.getUser().getEmail());
		if(authentication.getName().equals(post.getUser().getEmail())|
				authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))){
			return post;
		}
		return null;
	}
	@Override
	public Page<Post> paginatedPosts(Pageable pagination) {
		return postRepo.findAllByIsPublishedTrue(pagination);

	}

	public Page<Post> getUnpublishedPost(boolean isTrue,int start,int limit,Authentication authentication) {
		Pageable pageRequest = PageRequest.of(start / limit, limit);
		return postRepo.findPostsUnpublishedByUser(isTrue,authentication.getName(),pageRequest);
	}

	public void publishUpdatedPost(Post postToPublish,Authentication authentication) {
		User user=userRepo.findByEmail(authentication.getName())
				.orElseThrow(()->new RuntimeException("user not found"));
		postToPublish.setAuthor(user.getName());
		postToPublish.setUser(user);
		postToPublish.setPublishedAt(LocalDateTime.now());
		postToPublish.setIsPublished(true);
		this.updatePost(postToPublish);
	}

	@Override
	public List<Post> getFilteredPostsByUserAndTag(String tagId, Long authorId) {
		return postRepo.findPostIfUserHasThatTag(Long.parseLong(tagId), authorId);

	}

	@Override
	public Page<Post> getPaginatedItems(List<Long> filteredPostIds, Pageable paging) {
		return null;
	}

	public Page<Post> findAllByOrderByPublished(String order, Integer start,Integer limit) {
		Page<Post> sortedPosts;
		Pageable pagination;
		if (order.equals("asc")) {
			pagination=PageRequest.of(start/limit,limit,Sort.by("publishedAt").ascending());
		} else {
			pagination=PageRequest.of(start/limit,limit,Sort.by("publishedAt").descending());
		}
		sortedPosts = postRepo.findAllByIsPublishedTrue(pagination);
		return sortedPosts;
	}

	public void publishPost(Post postToPublish,Authentication authentication) {
		User user=userRepo.findByEmail(authentication.getName())
				.orElseThrow(()->new RuntimeException("user not found"));
		postToPublish.setAuthor(user.getName());
		postToPublish.setUser(user);
		postToPublish.setCreatedAt(LocalDateTime.now());
		postToPublish.setPublishedAt(LocalDateTime.now());
		postToPublish.setIsPublished(true);
		if (postToPublish.getContent().length() > 100) {
			postToPublish.setExcerpt(postToPublish.getContent().substring(0, 100));
		} else {
			postToPublish.setExcerpt(postToPublish.getContent());
		}
		addTags(postToPublish, postToPublish);

		postRepo.save(postToPublish);
	}


	public Page<Post> getPostsByDatesBetweenOrdered(String startDate,String endDate,Pageable pagination){
				return postRepo.findAllPostsByPublishedAtBetweenOrdered(startDate,endDate,pagination);
	}
}
