package com.example.blog.blogapp.serviceimpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.entity.Tag;
import com.example.blog.blogapp.entity.User;
import com.example.blog.blogapp.repository.PostRepository;
import com.example.blog.blogapp.repository.TagRepository;
import com.example.blog.blogapp.service.PostService;

@Service
public class PostServiceImpl implements PostService {
	@Autowired
	PostRepository blogRepo;
	@Autowired
	TagRepository tagRepo;
	
	@Override
	public List<Post> getBlogPosts() {
		System.out.println(blogRepo.findAll());
		return blogRepo.findAll(Sort.by(Sort.Direction.DESC,"id"));
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
			Optional<Tag> existingTag=tagRepo.findByNameIgnoreCase(tag);
			if(existingTag.isPresent()) {
				newPost.getTags().add(existingTag.get());
			}
			else {
			Tag tags=new Tag(tag,LocalDateTime.now());
			tags.getPostTag().add(newPost);
			newPost.getTags().add(tags);
			}
		}
		
		blogRepo.save(newPost);
		
	}
	
	public void updatePost(Post newPost) {
		Post postToUpdate=returnBlog(newPost.getId());
		postToUpdate.setContent(newPost.getContent());
		postToUpdate.setTitle(newPost.getTitle());
		postToUpdate.setUpdatedAt(LocalDateTime.now());
		postToUpdate.setTags(new ArrayList<>());
		if(newPost.getIsPublished()){
			postToUpdate.setIsPublished(true);
		}
		postToUpdate.setPublishedAt(newPost.getPublishedAt());
		List<String> tagList=Arrays.asList(newPost.getTagField().split(","));
		for(String tag:tagList) {
			Optional<Tag> existingTag=tagRepo.findByNameIgnoreCase(tag);
			if(existingTag.isPresent()) {
				postToUpdate.getTags().add(existingTag.get());
			}
			else {
			Tag tags=new Tag(tag,LocalDateTime.now());
			tags.getPostTag().add(postToUpdate);
			postToUpdate.getTags().add(tags);
			}
		}
		
		if(newPost.getContent().length()>100) {
			postToUpdate.setExcerpt(newPost.getContent().substring(0, 100));
			}
			else {
				postToUpdate.setExcerpt(newPost.getContent());
			}
		
		postToUpdate.setTagField(newPost.getTagField());
		blogRepo.save(postToUpdate);
	}
	
	public void deletePost(Long id) {
		Post postToBeDeleted=returnBlog(id);
		postToBeDeleted.setTags(new ArrayList<>());;
		blogRepo.deleteById(id);
		
	}
//	
	public Post returnBlog(Long id) {
		return blogRepo.findById(id).
				orElseThrow(()->new RuntimeException("Blog not present"));
	}
	
	
	public PostServiceImpl() {
	}

	@Override
	public Page<Post> paginatedPosts(Pageable pagination) {
		return blogRepo. findAllByIsPublishedTrue(pagination);
		
	}
	
	public Page<Post> getSearchedPosts(String searchString,Pageable paging) {
		Page<Post> posts=blogRepo.findByMultipleFieldsIgnoreCaseIn(searchString,paging);
	
		return posts;
	}
	
	public Page<Post> getUnpublishedPost(Pageable pageRequest){
		return blogRepo.findAllByIsPublishedFalse(pageRequest);
	}
	
	
	public void publishUpdatedPost(Post postToPublish) {
		postToPublish.setPublishedAt(LocalDateTime.now());
		postToPublish.setIsPublished(true);
		this.updatePost(postToPublish);
	}

	@Override
	public Optional<Post> getFilteredPostsByUserAndTag(String tagId,Long authorId) {
		return blogRepo.findPostIfUserHasThatTag(Long.parseLong(tagId),authorId);
		
	}
	
	public Page<Post> findAllByOrderByPublished(String order,Pageable pagination)
	{
		Page<Post> sortedPosts;
		if(order.equals("asc")) {
			sortedPosts=blogRepo.findAllByOrderByPublishedAtAsc(pagination);
		}
		else {
			sortedPosts=blogRepo.findAllByOrderByPublishedAtDesc(pagination);
		}
		return sortedPosts;
	}

	public void publishPost(Post postToPublish) {
		postToPublish.setCreatedAt(LocalDateTime.now());
		postToPublish.setPublishedAt(LocalDateTime.now());
		postToPublish.setIsPublished(true);
		if(postToPublish.getContent().length()>100) {
			postToPublish.setExcerpt(postToPublish.getContent().substring(0, 100));
			}
			else {
				postToPublish.setExcerpt(postToPublish.getContent());
			}
			List<String> tagList=Arrays.asList(postToPublish.getTagField().split(","));
			for(String tag:tagList) {
				Optional<Tag> existingTag=tagRepo.findByNameIgnoreCase(tag);
				if(existingTag.isPresent()) {
					postToPublish.getTags().add(existingTag.get());
				}
				else {
				Tag tags=new Tag(tag,LocalDateTime.now());
				tags.getPostTag().add(postToPublish);
				postToPublish.getTags().add(tags);
				}
			}
			
			blogRepo.save(postToPublish);
	}

}
