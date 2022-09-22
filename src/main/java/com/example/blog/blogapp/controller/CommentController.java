package com.example.blog.blogapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.example.blog.blogapp.serviceimpl.CommentServicImpl;

@Controller
@RequestMapping("/comment")
public class CommentController {
	
	@Autowired
	CommentServicImpl commentService;
	@GetMapping("/delete")
	public String deleteComment(@RequestParam("id") String id) {
		String postId=commentService.deleteComment(Long.parseLong(id));
		
		return "redirect:/view?id="+postId;
	}
}
