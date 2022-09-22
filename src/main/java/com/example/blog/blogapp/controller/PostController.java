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
import com.example.blog.blogapp.service.CommentService;
import com.example.blog.blogapp.serviceimpl.BlogServiceImpl;
import com.example.blog.blogapp.serviceimpl.CommentServicImpl;

@Controller
public class PostController {
	@Autowired
	BlogServiceImpl repo;

	@Autowired
	CommentServicImpl commentRepo;
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
		Comment newComment=new Comment();
		model.addAttribute("newcomment",newComment);
		model.addAttribute("id",id);
		return "view.html";
	}
	@GetMapping("/update")
	public String updatePost(@RequestParam("id") String id,Model model) {
		model.addAttribute("blog", repo.returnBlog(Long.parseLong(id)));
		model.addAttribute("id",id);
		return "update.html";
	}
	
	@PostMapping("/update")
	public String updatePost(@ModelAttribute Post updatedPost) {
		System.out.print(updatedPost);
		repo.updatePost(updatedPost);
		return "redirect:/";
	}
	
	@PostMapping("/addcomment")
	public String addComment(@ModelAttribute("newcomment") Comment comment,@RequestParam("id") long id) {
		commentRepo.createComment(comment, id);
		return "redirect:/view?id="+id;
	}
	
	@GetMapping("/delete")
	public String deletePost(@RequestParam("id") long id) {
		repo.deletePost(id);
		return "redirect:/";
	}
}