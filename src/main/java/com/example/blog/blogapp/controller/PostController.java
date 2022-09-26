package com.example.blog.blogapp.controller;



import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.blog.blogapp.entity.Comment;
import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.entity.Tag;
import com.example.blog.blogapp.entity.User;
import com.example.blog.blogapp.repository.PostRepository;
import com.example.blog.blogapp.repository.UserRepository;
import com.example.blog.blogapp.serviceimpl.PostServiceImpl;
import com.example.blog.blogapp.serviceimpl.CommentServicImpl;
import com.example.blog.blogapp.serviceimpl.TagServiceImpl;
import com.example.blog.blogapp.serviceimpl.UserServiceImpl;

@Controller
public class PostController {
	@Autowired
	PostServiceImpl blogService;
	
	@Autowired
	TagServiceImpl tagService;
	@Autowired
	CommentServicImpl commentService;
	@Autowired
	UserServiceImpl userService;
	
	
	@GetMapping
	public String getHomePage(Model model,@RequestParam(value = "start",required = false,defaultValue = "0") Integer start,
			@RequestParam(value = "limit",required = false,defaultValue = "4") Integer limit,
			@RequestParam(value = "search",required = false) String searchField,
			@RequestParam(value="authorId",required=false) String[] authorId,
			@RequestParam(value="tagId",required=false) String[] tagId,
			@RequestParam(value="order",required=false) String order){
		model.addAttribute("tagNames",tagService.getLinkedTags());	
		List<User> users=userService.getAllUsers();
		model.addAttribute("userNames", users);
		if(order!=null) {
			model.addAttribute("order", order);
			Pageable pagination=PageRequest.of(start/limit, limit);
			model.addAttribute("startIndex",start);
			
			model.addAttribute("limit",limit);
			if(order.equals("asc")) {
					Page<Post> paginatedPosts=blogService.findAllByOrderByPublished(order, pagination);
					model.addAttribute("posts",paginatedPosts);
					model.addAttribute("totalElements",paginatedPosts.getTotalElements());
			}
			else {
				Page<Post> paginatedPosts=blogService.findAllByOrderByPublished(order, pagination);
			model.addAttribute("posts", paginatedPosts);
			model.addAttribute("totalElements",paginatedPosts.getTotalElements());
			}
			
				
		}
		else if(authorId!=null | tagId!=null) {
			Set<Post> filteredPosts=new HashSet<>();
			
			if(tagId!=null && authorId!=null) {
				for(String userId:authorId) {
					User author=userService.getUserById(Long.parseLong(userId));
					for(String tag:tagId) {
						Optional<Post> postToBeAdded=blogService.getFilteredPostsByUserAndTag(tag, author.getId());
						if(postToBeAdded.isPresent()) {
							filteredPosts.add(postToBeAdded.get());
						}
					}
				}
			}
			else if(tagId!=null) {
				for(String id:tagId) {
					Tag fetchedTag=tagService.getTagById(Long.parseLong(id));
					filteredPosts.addAll(fetchedTag.getPostTag());
				}
			}
			else {
				for(String id:authorId) {
					User author=userService.getUserById(Long.parseLong(id));
					filteredPosts.addAll(author.getPostsByUser());
				}
			}
			model.addAttribute("posts", filteredPosts);
		}
		
	else if(searchField!=null) {
			model.addAttribute("search", searchField);
			Pageable pagination=PageRequest.of(start/limit, limit);
			Page<Post> paginatedItems=blogService.getSearchedPosts(searchField,pagination);
			model.addAttribute("posts",paginatedItems);
			model.addAttribute("startIndex",start);
			model.addAttribute("totalElements",paginatedItems.getTotalElements());
			model.addAttribute("limit",limit);
		}
		else{
			Pageable pagination=PageRequest.of(start/limit, limit);
			Page<Post> pageItems=blogService.paginatedPosts(pagination);
			model.addAttribute("posts", pageItems);
			model.addAttribute("totalElements",pageItems.getTotalElements());
			model.addAttribute("startIndex",start);
			model.addAttribute("limit",limit);
		}
		
		return "posts.html";
	}

	@GetMapping("/create")
	public String createPage(Model model) {
		model.addAttribute("post", new Post());
		return "createpost.html";
	}

	@PostMapping("/create")
	public String createPost(@ModelAttribute Post post) {
		blogService.createPost(post);
		return "redirect:/";
	} 
	
	@GetMapping("/view")
	public String viewPost(@RequestParam("id") String id,Model model,@RequestParam(value = "commentId",required = false) String commentId) {
		model.addAttribute("blog", blogService.returnBlog(Long.parseLong(id)));
		if(commentId!=null) {
			Comment newComment=commentService.returnComment(Long.parseLong(commentId));
			model.addAttribute("newcomment",newComment);
		}
		else {
		Comment newComment=new Comment();
		model.addAttribute("newcomment",newComment);
		}
		
		model.addAttribute("id",id);
		return "view.html";
	}
	@GetMapping("/update")
	public String updatePost(@RequestParam("id") String id,Model model) {
		model.addAttribute("blog", blogService.returnBlog(Long.parseLong(id)));
		model.addAttribute("id",id);
		return "update.html";
	}
	
	@PostMapping("/update")
	public String updatePost(@ModelAttribute("blog") Post updatedPost) {
		System.out.print(updatedPost);
		blogService.updatePost(updatedPost);
		return "redirect:/";
	}
	
	
	@GetMapping("/delete")
	public String deletePost(@RequestParam("id") long id) {
		blogService.deletePost(id);
		return "redirect:/";
	}
	
	@GetMapping("/draft")
	public String postNotPublished(@RequestParam("start") int start, Model model) {
		Pageable pagination=PageRequest.of(start/5, 5);
		Page<Post> pageItems=blogService.getUnpublishedPost(pagination);
		model.addAttribute("posts", pageItems);
		model.addAttribute("totalElements",pageItems.getTotalElements());
		model.addAttribute("startIndex",start);
		model.addAttribute("limit",5);
		return "draft.html";
	}
	
	@PostMapping("/publish")
	public String publishPost(@ModelAttribute("blog") Post postToPublish) {
		blogService.publishPost(postToPublish);
//		System.out.println(postToPublish);
		return "redirect:/draft?start=0";
	}
	
	
	
}
