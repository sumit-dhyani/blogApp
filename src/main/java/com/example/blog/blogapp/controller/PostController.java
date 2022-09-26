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
import com.example.blog.blogapp.serviceimpl.PostServiceImpl;
import com.example.blog.blogapp.serviceimpl.CommentServicImpl;
import com.example.blog.blogapp.serviceimpl.TagServiceImpl;
import com.example.blog.blogapp.serviceimpl.UserServiceImpl;

@Controller
public class PostController {
	@Autowired
	PostServiceImpl postService;
	@Autowired
	TagServiceImpl tagService;
	@Autowired
	CommentServicImpl commentService;
	@Autowired
	UserServiceImpl userService;

	@GetMapping
	public String getHomePage(Model model,
			@RequestParam(value = "start", required = false, defaultValue = "0") Integer start,
			@RequestParam(value = "limit", required = false, defaultValue = "4") Integer limit,
			@RequestParam(value = "search", required = false) String searchField,
			@RequestParam(value = "authorId", required = false) String[] authorId,
			@RequestParam(value = "tagId", required = false) String[] tagId,
			@RequestParam(value = "order", required = false) String order) {
		model.addAttribute("tagNames", tagService.getLinkedTags());
		List<User> users = userService.getAllUsers();
		model.addAttribute("userNames", users);
		if (order != null) {
			model.addAttribute("order", order);
			Pageable pagination = PageRequest.of(start / limit, limit);
			model.addAttribute("startIndex", start);

			model.addAttribute("limit", limit);
			if (order.equals("asc")) {
				Page<Post> paginatedPosts = postService.findAllByOrderByPublished(order, pagination);
				model.addAttribute("posts", paginatedPosts);
				model.addAttribute("totalElements", paginatedPosts.getTotalElements());
			} else {
				Page<Post> paginatedPosts = postService.findAllByOrderByPublished(order, pagination);
				model.addAttribute("posts", paginatedPosts);
				model.addAttribute("totalElements", paginatedPosts.getTotalElements());
			}

		} else if (authorId != null | tagId != null) {
			Set<Post> filteredPosts = new HashSet<>();
			StringJoiner filteredQuery = new StringJoiner("&authorId=", "&authorId=", "");
			StringJoiner tagQuery = new StringJoiner("&tagId=", "&tagId=", "");
			if (tagId != null && authorId != null) {

				for (String userId : authorId) {
					filteredQuery.add(userId);
					User author = userService.getUserById(Long.parseLong(userId));
					for (String tag : tagId) {
						tagQuery.add(tag);
						List<Post> postToBeAdded = postService.getFilteredPostsByUserAndTag(tag, author.getId());
						if (postToBeAdded.size()>0) {
							filteredPosts.addAll(postToBeAdded);
						}
					}
				}
				model.addAttribute("filter", filteredQuery.merge(tagQuery));
			} else if (tagId != null) {
				for (String id : tagId) {
					tagQuery.add(id);
					Tag fetchedTag = tagService.getTagById(Long.parseLong(id));
					filteredPosts.addAll(fetchedTag.getPostTag());
				}
				model.addAttribute("filter", tagQuery);
			} else {
				for (String id : authorId) {
					filteredQuery.add(id);
					User author = userService.getUserById(Long.parseLong(id));
					filteredPosts.addAll(author.getPostsByUser());
				}
				model.addAttribute("filter", filteredQuery);
			}
			List<Long> filteredPostIds = new ArrayList<>();
			for (Post posts : filteredPosts) {
				filteredPostIds.add(posts.getId());
			}
			System.out.println(filteredPostIds);
			
			Pageable pagination = PageRequest.of(start / limit, limit);
			Page<Post> paginatedPosts = postService.getPaginatedItems(filteredPostIds, pagination);
			model.addAttribute("posts", paginatedPosts);
			model.addAttribute("startIndex", start);
			model.addAttribute("totalElements", paginatedPosts.getTotalElements());
			System.out.println(paginatedPosts.getTotalElements());
			model.addAttribute("limit", limit);

		}

		else if (searchField != null) {
			model.addAttribute("search", searchField);
			Pageable pagination = PageRequest.of(start / limit, limit);
			Page<Post> paginatedItems = postService.getSearchedPosts(searchField, pagination);
			model.addAttribute("posts", paginatedItems);
			model.addAttribute("startIndex", start);
			model.addAttribute("totalElements", paginatedItems.getTotalElements());
			model.addAttribute("limit", limit);
		} else {
			Pageable pagination = PageRequest.of(start / limit, limit);
			Page<Post> pageItems = postService.paginatedPosts(pagination);
			model.addAttribute("posts", pageItems);
			model.addAttribute("totalElements", pageItems.getTotalElements());
			model.addAttribute("startIndex", start);
			model.addAttribute("limit", limit);
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
		postService.createPost(post);
		return "redirect:/";
	}

	@GetMapping("/view")
	public String viewPost(@RequestParam("id") String id, Model model,
			@RequestParam(value = "commentId", required = false) String commentId) {
		model.addAttribute("blog", postService.returnBlog(Long.parseLong(id)));
		if (commentId != null) {
			Comment newComment = commentService.returnComment(Long.parseLong(commentId));
			model.addAttribute("newcomment", newComment);
			model.addAttribute("commentId", commentId);
		} else {
			Comment newComment = new Comment();
			model.addAttribute("newcomment", newComment);
		}

		model.addAttribute("id", id);
		return "view.html";
	}

	@GetMapping("/update")
	public String updatePost(@RequestParam("id") String id, Model model) {
		model.addAttribute("blog", postService.returnBlog(Long.parseLong(id)));
		model.addAttribute("id", id);
		return "update.html";
	}

	@PostMapping("/update")
	public String updatePost(@ModelAttribute("blog") Post updatedPost) {
		postService.updatePost(updatedPost);
		return "redirect:/";
	}

	@GetMapping("/delete")
	public String deletePost(@RequestParam("id") long id) {
		postService.deletePost(id);
		return "redirect:/";
	}

	@GetMapping("/draft")
	public String postNotPublished(@RequestParam("start") int start, Model model,
			@RequestParam(value = "limit", defaultValue = "4") int limit) {
		Page<Post> paginatedPosts = postService.getUnpublishedPost(start, limit);
		model.addAttribute("posts", paginatedPosts);
		model.addAttribute("totalElements", paginatedPosts.getTotalElements());
		model.addAttribute("startIndex", start);
		model.addAttribute("limit", 4);
		return "draft.html";
	}

	@PostMapping("/publish")
	public String publishPost(@ModelAttribute("blog") Post postToPublish) {
		postService.publishUpdatedPost(postToPublish);
		return "redirect:/draft?start=0";
	}

	@PostMapping("/publishnew")
	public String publishNewPost(@ModelAttribute("blog") Post postToPublish) {
		postService.publishPost(postToPublish);
		return "redirect:/";
	}

}
