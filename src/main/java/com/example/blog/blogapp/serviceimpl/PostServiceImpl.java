package com.example.blog.blogapp.serviceimpl;

import java.time.OffsetDateTime;
import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.entity.Tag;
import com.example.blog.blogapp.repository.PostRepository;
import com.example.blog.blogapp.repository.TagRepository;
import com.example.blog.blogapp.service.PostService;

@Service
public class PostServiceImpl implements PostService {
	@Autowired
	PostRepository postRepo;
	@Autowired
	TagRepository tagRepo;

	@Override
	public List<Post> getBlogPosts() {
		System.out.println(postRepo.findAll());
		return postRepo.findAll(Sort.by(Sort.Direction.DESC, "id"));
	}

	@Override
	public void createPost(Post newPost) {
		newPost.setAuthor("Anonymous");
		newPost.setCreatedAt(LocalDateTime.now());
		newPost.setIsPublished(false);
		if (newPost.getContent().length() > 100) {
			newPost.setExcerpt(newPost.getContent().substring(0, 100));
		} else {
			newPost.setExcerpt(newPost.getContent());
		}
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
		List<String> tagList = List.of(newPost.getTagField().split(","));
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

	public void deletePost(Long id) {
		Post postToBeDeleted = returnBlog(id);
		postToBeDeleted.setTags(new ArrayList<>());
		postRepo.deleteById(id);

	}

//	
	public Post returnBlog(Long id) {
		return postRepo.findById(id).orElseThrow(() -> new RuntimeException("Blog not present"));
	}

	public PostServiceImpl() {
	}

	@Override
	public Page<Post> paginatedPosts(Pageable pagination) {
		return postRepo.findAllByIsPublishedTrue(pagination);

	}

	public Page<Post> getSearchedPosts(String searchString, Pageable paging) {
		return postRepo.findByMultipleFieldsIgnoreCaseIn(searchString, paging);
	}

	public List<Post> getSearchedPosts(String searchString) {

		return postRepo.findByMultipleFieldsIgnoreCaseIn(searchString);
	}

	public Page<Post> getUnpublishedPost(int start,int limit) {
		Pageable pageRequest = PageRequest.of(start / limit, limit);
		return postRepo.findAllByIsPublishedFalse(pageRequest);
	}

	public void publishUpdatedPost(Post postToPublish) {
		postToPublish.setPublishedAt(LocalDateTime.now());
		postToPublish.setIsPublished(true);
		this.updatePost(postToPublish);
	}

	@Override
	public List<Post> getFilteredPostsByUserAndTag(String tagId, Long authorId) {
		return postRepo.findPostIfUserHasThatTag(Long.parseLong(tagId), authorId);

	}

	public Page<Post> findAllByOrderByPublished(String order, Pageable pagination) {
		Page<Post> sortedPosts;
		if (order.equals("asc")) {
			sortedPosts = postRepo.findSortedPublishedPostAsc(pagination);
		} else {
			sortedPosts = postRepo.findSortedPublishedPostDesc(pagination);
		}
		return sortedPosts;
	}

	public void publishPost(Post postToPublish) {
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

	public Page<Post> getPaginatedItems(List<Long> filteredPostIds,Pageable paging) {
		return postRepo.getResultsById(filteredPostIds, paging);
	}




	public List<Post> getPostsBetweenStartAndEndDate(LocalDate startDate, OffsetDateTime endDate){
		return postRepo.findAllByPublishedAtLessThanEqualAndPublishedAtGreaterThanEqual(endDate, OffsetDateTime.from(startDate));
	}


	public Set<Post> getPostsByTagId(String[] ids){
		List<Long> tagIds = new ArrayList<>();
		for(String id:ids){
			tagIds.add(Long.valueOf(id));
		}
		return postRepo.finAllPostsByTag(tagIds);
	}


	public Set<Post> getPostsByUserAndTagId(String[] tIds,String[] uIds){
		List<Long> tagIds=new ArrayList<>();
		List<Long> userIds=new ArrayList<>();
		for(String id:tIds){
			tagIds.add(Long.valueOf(id));
		}
		for(String id:uIds){
			userIds.add(Long.valueOf(id));
		}
		return postRepo.findPostsByUserAndTags(tagIds,userIds);
	}

	public Page<Post> getPostsByUserAndTagIdSorted(String[] tIds,String[] uIds,String order,Pageable pagination){
		List<Long> tagIds=new ArrayList<>();
		List<Long> userIds=new ArrayList<>();
		for(String id:tIds){
			tagIds.add(Long.valueOf(id));
		}
		for(String id:uIds){
			userIds.add(Long.valueOf(id));
		}
		Set<Post> postsByUidAndTid=postRepo.findPostsByUserAndTags(tagIds,userIds);
		List<Long> postId=new ArrayList<>();
		for(Post p:postsByUidAndTid){
			postId.add(p.getId());
		}


		return postRepo.getResultsById(postId,pagination);
	}

	public Page<Post> getPostsByAuthorSorted(Integer start, Integer limit, String order, String[] authorId) {
		Pageable pagination;
		if(order.equals("asc")){
			pagination = PageRequest.of(start / limit, limit, Sort.by("published_at").ascending());
		}
		else{
			pagination = PageRequest.of(start / limit, limit,Sort.by("published_at").descending());
		}
		List<Long> userIds=new ArrayList<>();
		for(String uId:authorId){
			userIds.add(Long.valueOf(uId));
		}
		return postRepo.findUsersWithIdSorted(userIds,pagination);
	}

	public Page<Post> getPostsByTagIdSorted(Integer start, Integer limit, String order, String[] tagId) {
		Pageable pagination;
		if(order.equals("asc")){
			pagination = PageRequest.of(start / limit, limit, Sort.by("published_at").ascending());
		}
		else{
			pagination = PageRequest.of(start / limit, limit,Sort.by("published_at").descending());
		}
		List<Long> tagIds=new ArrayList<>();
		for(String uId:tagId){
			tagIds.add(Long.valueOf(uId));
		}
		return postRepo.findPostsByTagSorted(tagIds,pagination);
	}

	public Page<Post> getPostsByDatesBetweenOrdered(String startDate,String endDate,Pageable pagination){
				return postRepo.findAllPostsByPublishedAtBetweenOrdered(startDate,endDate,pagination);
	}
}
