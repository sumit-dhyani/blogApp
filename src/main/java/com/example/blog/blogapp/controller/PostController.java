package com.example.blog.blogapp.controller;


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
import com.example.blog.blogapp.repository.BlogRepository;
import com.example.blog.blogapp.serviceimpl.BlogServiceImpl;
import com.example.blog.blogapp.serviceimpl.CommentServicImpl;

@Controller
public class PostController {
	@Autowired
	BlogServiceImpl blogService;
	
	@Autowired
	BlogRepository blogRepo;
	@Autowired
	CommentServicImpl commentService;
	@GetMapping
	public String getHomePage(Model model,@RequestParam(value = "start",required = false) Integer start,@RequestParam(value = "limit",required = false) Integer limit) {
		if(start!=null && limit!=null) {
			Pageable pagination=PageRequest.of(start/limit, limit);
			Page<Post> pageItems=blogService.paginatedPosts(pagination);
			model.addAttribute("posts", pageItems);
			model.addAttribute("totalElements",pageItems.getTotalElements());
			model.addAttribute("startIndex",start);
			model.addAttribute("limit",limit);
		}
		else {
		model.addAttribute("posts", blogService.getBlogPosts());
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
	public String viewPost(@RequestParam("id") String id,Model model) {
		model.addAttribute("blog", blogService.returnBlog(Long.parseLong(id)));
		Comment newComment=new Comment();
		model.addAttribute("newcomment",newComment);
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
}
