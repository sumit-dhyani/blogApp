package com.example.blog.blogapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.blog.blogapp.entity.Comment;
import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.serviceimpl.BlogServiceImpl;

@Controller
public class FrontController {
	@Autowired
	BlogServiceImpl repo;

	
	@GetMapping
	public String getHomePage(Model model) {
		model.addAttribute("posts", repo.getBlogPosts());
		return "posts.html";
	}

	@GetMapping("/create")
	public String createPage(Model model) {
		model.addAttribute("post", new Post());
		return "createpost.html";
	}
	
	@PostMapping("/create")
	public String createPost(@ModelAttribute Post post) {
		repo.createPost(post);
		return "redirect:/";
	} 
	
	@GetMapping("/view")
	public String viewPost(@RequestParam("id") String id,Model model) {
		model.addAttribute("blog", repo.returnBlog(Long.parseLong(id)));
		model.addAttribute("comment",new Comment());
		model.addAttribute("id",id);
		return "view.html";
	}
	@GetMapping("/update")
	public String updatePost(@RequestParam("id") String id,Model model) {
		model.addAttribute("blog", repo.returnBlog(Long.parseLong(id)));
		model.addAttribute("id",id);
		return "update.html";
	}
}
