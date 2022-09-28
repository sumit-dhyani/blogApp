package com.example.blog.blogapp.controller;

import com.example.blog.blogapp.entity.Comment;
import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.entity.Tag;
import com.example.blog.blogapp.entity.User;
import com.example.blog.blogapp.serviceimpl.CommentServicImpl;
import com.example.blog.blogapp.serviceimpl.PostServiceImpl;
import com.example.blog.blogapp.serviceimpl.TagServiceImpl;
import com.example.blog.blogapp.serviceimpl.UserServiceImpl;
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

import java.util.*;

@Controller
public class PostController {
    public static final String LIMIT = "4";
    public static final String START_INDEX = "0";
    public static final String START = "start";
    public static final String LIMIT_PARAM = "limit";
    public static final String SEARCH = "search";
    public static final String AUTHOR_ID = "authorId";
    public static final String TAG_ID = "tagId";
    public static final String ORDER = "order";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";

    private final PostServiceImpl postService;
    private final TagServiceImpl tagService;

    private final CommentServicImpl commentService;

    private final UserServiceImpl userService;

    @Autowired
    PostController(PostServiceImpl postService, TagServiceImpl tagService, CommentServicImpl commentService, UserServiceImpl userService) {
        this.postService = postService;
        this.tagService = tagService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping
    public String getHomePage(Model model,
                              @RequestParam(value = START, required = false, defaultValue = START_INDEX) Integer start,
                              @RequestParam(value = LIMIT_PARAM, required = false, defaultValue = LIMIT) Integer limit,
                              @RequestParam(value = SEARCH, required = false) String searchField,
                              @RequestParam(value = AUTHOR_ID, required = false) String[] authorId,
                              @RequestParam(value = TAG_ID, required = false) String[] tagId,
                              @RequestParam(value = ORDER, required = false) String order,
                              @RequestParam(value = START_DATE, required = false) String startDate,
                              @RequestParam(value = END_DATE, required = false) String endDate) {
        model.addAttribute(SEARCH, searchField);
        model.addAttribute("tagNames", tagService.getLinkedTags());
        List<User> users = userService.getAllUsers();
        model.addAttribute("tagIds", tagId);
        model.addAttribute("userNames", users);
        if (order != null) {
            model.addAttribute(ORDER, order);
            Pageable pagination = PageRequest.of(start / limit, limit);
            model.addAttribute("startIndex", start);
            model.addAttribute(LIMIT_PARAM, limit);
            Page<Post> paginatedPosts = postService.findAllByOrderByPublished(order, pagination);
            model.addAttribute("posts", paginatedPosts);
            model.addAttribute("totalElements", paginatedPosts.getTotalElements());

        } else if (authorId != null | tagId != null) {
            Set<Post> filteredPosts = new HashSet<>();
            StringJoiner filteredQuery = new StringJoiner("&authorId=", "&authorId=", "");
            StringJoiner tagQuery = new StringJoiner("&tagId=", "&tagId=", "");
            StringJoiner dateQuery = new StringJoiner("&endDate", "&startDate", "");
            if (tagId != null && authorId != null && searchField != null) {
                List<Post> searchedPosts = postService.getSearchedPosts(searchField);
                Set<Post> filtered = new HashSet<>();
				getParameters(authorId, tagId, filtered, filteredQuery, tagQuery);
				for (Post ifResultsIncludeSearchItems : searchedPosts) {
                    if (filtered.contains(ifResultsIncludeSearchItems)) {
                        filteredPosts.add(ifResultsIncludeSearchItems);
                    }
                }
                model.addAttribute("filter", filteredQuery.merge(tagQuery) + "&search=" + searchField);
            } else if (tagId != null && authorId != null) {
				getParameters(authorId, tagId, filteredPosts, filteredQuery, tagQuery);
				model.addAttribute("filter", filteredQuery.merge(tagQuery));
            }
//		 if (startDate!=null && endDate!=null) {
//					List<Post> postsReturned=postService.getPostsBetweenStartAndEndDate(LocalDate.parse(startDate), OffsetDateTime.parse(endDate));
//					filteredPosts.addAll(postsReturned);
//					dateQuery.add(startDate).add(endDate);
//					model.addAttribute("filter",dateQuery);
            else if (tagId != null && searchField != null) {
                List<Post> searchedPosts = postService.getSearchedPosts(searchField);
                Set<Post> filtered = new HashSet<>();
                for (String id : tagId) {
                    tagQuery.add(id);
                    Tag fetchedTag = tagService.getTagById(Long.parseLong(id));
                    filtered.addAll(fetchedTag.getPostTag());
                }
                for (Post ifResultsIncludeSearchItems : searchedPosts) {
                    if (filtered.contains(ifResultsIncludeSearchItems)) {
                        filteredPosts.add(ifResultsIncludeSearchItems);
                    }
                }
                model.addAttribute("filter", tagQuery + "&search" + searchField);

            } else if (tagId != null) {
                for (String id : tagId) {
                    tagQuery.add(id);
                    Tag fetchedTag = tagService.getTagById(Long.parseLong(id));
                    filteredPosts.addAll(fetchedTag.getPostTag());
                }
                model.addAttribute("filter", tagQuery);
            } else if (authorId != null && searchField != null) {
                List<Post> searchedPosts = postService.getSearchedPosts(searchField);
                Set<Post> filtered = new HashSet<>();
                for (String id : authorId) {
                    filteredQuery.add(id);
                    User author = userService.getUserById(Long.parseLong(id));
                    filteredPosts.addAll(author.getPostsByUser());
                }
                for (Post ifResultsIncludeSearchItems : searchedPosts) {
                    if (filtered.contains(ifResultsIncludeSearchItems)) {
                        filteredPosts.add(ifResultsIncludeSearchItems);
                    }
                }
                model.addAttribute("filter", filteredQuery + "&search" + searchField);
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

            Pageable pagination = PageRequest.of(start / limit, limit);
            Page<Post> paginatedPosts = postService.getPaginatedItems(filteredPostIds, pagination);
            model.addAttribute("posts", paginatedPosts);
            model.addAttribute("startIndex", start);
            model.addAttribute("totalElements", paginatedPosts.getTotalElements());
            model.addAttribute(LIMIT_PARAM, limit);

        } else if (searchField != null) {

            Pageable pagination = PageRequest.of(start / limit, limit);
            Page<Post> paginatedItems = postService.getSearchedPosts(searchField, pagination);
            model.addAttribute("posts", paginatedItems);
            model.addAttribute("startIndex", start);
            model.addAttribute("totalElements", paginatedItems.getTotalElements());
            model.addAttribute(LIMIT_PARAM, limit);
        } else {
            Pageable pagination = PageRequest.of(start / limit, limit);
            Page<Post> pageItems = postService.paginatedPosts(pagination);
            model.addAttribute("posts", pageItems);
            model.addAttribute("totalElements", pageItems.getTotalElements());
            model.addAttribute("startIndex", start);
            model.addAttribute(LIMIT_PARAM, limit);
        }

        return "posts.html";
    }

	private void getParameters(@RequestParam(value = AUTHOR_ID, required = false) String[] authorId, @RequestParam(value = TAG_ID, required = false) String[] tagId, Set<Post> filteredPosts, StringJoiner filteredQuery, StringJoiner tagQuery) {
		for (String userId : authorId) {
			filteredQuery.add(userId);
			User author = userService.getUserById(Long.parseLong(userId));
			for (String tag : tagId) {
				tagQuery.add(tag);
				List<Post> postToBeAdded = postService.getFilteredPostsByUserAndTag(tag, author.getId());
				if (postToBeAdded.size() > 0) {
					filteredPosts.addAll(postToBeAdded);
				}
			}
		}
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
    public String postNotPublished(@RequestParam(START) int start, Model model,
                                   @RequestParam(value = LIMIT_PARAM, defaultValue = LIMIT) int limit) {
        Page<Post> paginatedPosts = postService.getUnpublishedPost(start, limit);
        model.addAttribute("posts", paginatedPosts);
        model.addAttribute("totalElements", paginatedPosts.getTotalElements());
        model.addAttribute("startIndex", start);
        model.addAttribute(LIMIT_PARAM, 4);
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
