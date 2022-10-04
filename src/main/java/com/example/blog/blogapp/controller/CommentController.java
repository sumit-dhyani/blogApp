package com.example.blog.blogapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.blog.blogapp.entity.Comment;
import com.example.blog.blogapp.serviceimpl.CommentServiceImpl;

@Controller
@RequestMapping("/comment")
public class CommentController {

	@Autowired
	CommentServiceImpl commentService;

	@GetMapping("/delete")
	public String deleteComment(@RequestParam("id") String id, Authentication authentication) {
		String userEmail=authentication.getName();
		String authorEmail=commentService.returnComment(Long.parseLong(id)).getPost().getUser().getEmail();
		if (userEmail.equals(authorEmail)|
				authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
			String postId = commentService.deleteComment(Long.parseLong(id));
			return "redirect:/view?id=" + postId;
		}
		else{
			return "error-404.html";
		}
	}

	@GetMapping("/update")
	public String updateComment(@RequestParam("id") String id, Model model) {
		Comment comment = commentService.returnComment(Long.parseLong(id));
		model.addAttribute("existing", comment);
		model.addAttribute("postid", comment.getPost().getId());
		return "updatecomment.html";
	}

	@PostMapping("/add")
	public String addComment(@ModelAttribute("newcomment") Comment comment, @RequestParam("id") long id,
			@RequestParam(value = "commentId", required = false) String commentId,
							 Authentication authentication) {
		if (commentId != null) {
			commentService.updateComment(comment, Long.parseLong(commentId));
		} else {
			commentService.createComment(comment, id,authentication);
		}
		return "redirect:/view?id=" + id;
	}
}
