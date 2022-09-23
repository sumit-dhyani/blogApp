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
import com.example.blog.blogapp.repository.BlogRepository;
import com.example.blog.blogapp.repository.TagRepository;
import com.example.blog.blogapp.service.BlogService;

@Service
public class BlogServiceImpl implements BlogService {
	@Autowired
	BlogRepository blogRepo;
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
			Optional<Tag> existingTag=tagRepo.findByName(tag);
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
		List<String> tagList=Arrays.asList(newPost.getTagField().split(","));
		for(String tag:tagList) {
			Optional<Tag> existingTag=tagRepo.findByName(tag);
			if(existingTag.isPresent()) {
				postToUpdate.getTags().add(existingTag.get());
			}
			else {
			Tag tags=new Tag(tag,LocalDateTime.now());
			tags.getPostTag().add(postToUpdate);
			postToUpdate.getTags().add(tags);
			}
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
	
	
	public BlogServiceImpl() {
	}

	@Override
	public Page<Post> paginatedPosts(Pageable pagination) {
		return blogRepo.findAll(pagination);
		
	}
	
	public List<Post> getSearchedPosts(String searchString) {
		List<Post> posts=blogRepo.findByMultipleFieldsIgnoreCaseIn(searchString);
	
		return posts;
	}
	

}
