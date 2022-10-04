package com.example.blog.blogapp.controller;

import com.example.blog.blogapp.entity.Comment;
import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.entity.User;
import com.example.blog.blogapp.repository.PostRepository;
import com.example.blog.blogapp.serviceimpl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.StringJoiner;

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
    private final CommentServiceImpl commentService;
    private final UserServiceImpl userService;
    private final PostDataServiceImpl postDataService;
    @Autowired
    PostRepository postrepo;

    @Autowired
    PostController(PostServiceImpl postService, TagServiceImpl tagService,
                   CommentServiceImpl commentService, UserServiceImpl userService, PostDataServiceImpl postDataService) {
        this.postService = postService;
        this.tagService = tagService;
        this.commentService = commentService;
        this.userService = userService;
        this.postDataService = postDataService;
    }

    @GetMapping
    public String getHomePage(Model model,
                              Authentication authentication,
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
        model.addAttribute("userNames", userService.getAllUsers());
        model.addAttribute(ORDER, order);
        model.addAttribute("startIndex", start);
        model.addAttribute(LIMIT_PARAM, limit);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        Page<Post> paginatedPosts;
        Pageable pagination = PageRequest.of(start / limit, limit);
        if (authentication != null) {
            model.addAttribute("loggedIn", true);
            model.addAttribute("userName", userService.getUserName(authentication.getName()));
        }
        if (order != null) {
            StringJoiner userQuery = new StringJoiner("&authorId=", "&authorId=", "");
            StringJoiner tagQuery = new StringJoiner("&tagId=", "&tagId=", "");
            if (searchField != null) {
                pagination = order.equals("asc")
                        ? PageRequest.of(start / limit, limit, Sort.by("published_at").ascending())
                        : PageRequest.of(start / limit, limit, Sort.by("published_at").descending());
                paginatedPosts = postDataService.searchedPosts(searchField, pagination);
            } else if (startDate != null) {
                if (order.equals("asc")) {
                    pagination = PageRequest.of(start / limit, limit, Sort.by("published_at").ascending());
                } else {
                    pagination = PageRequest.of(start / limit, limit, Sort.by("published_at").descending());
                }
                paginatedPosts = postService.getPostsByDatesBetweenOrdered(startDate,
                        LocalDate.parse(endDate).plusDays(1).toString(), pagination);
            } else if (authorId != null | tagId != null) {
                if (authorId != null && tagId != null) {
                    getParameters(authorId, tagId, userQuery, tagQuery);
                    model.addAttribute("filter", userQuery.toString() + tagQuery);
                } else if (authorId != null) {
                    getParameters(authorId, userQuery);
                    model.addAttribute("filter", userQuery.toString());
                } else if (tagId != null) {
                    getParameters(tagId, tagQuery);
                    model.addAttribute("filter", tagQuery.toString());
                }
                if (order.equals("asc")) {
                    pagination = PageRequest.of(start / limit, limit, Sort.by("publishedAt").ascending());
                } else {
                    pagination = PageRequest.of(start / limit, limit, Sort.by("publishedAt").descending());
                }
                paginatedPosts = postDataService.filteredPosts(authorId, tagId, "", pagination);
            } else {
                paginatedPosts = postService.findAllByOrderByPublished(order, start, limit);
            }
        } else if (authorId != null | tagId != null) {
            StringJoiner userQuery = new StringJoiner("&authorId=", "&authorId=", "");
            StringJoiner tagQuery = new StringJoiner("&tagId=", "&tagId=", "");
            if (tagId != null && authorId != null && searchField != null) {
                getParameters(authorId, tagId, userQuery, tagQuery);
                model.addAttribute("filter", userQuery.toString()
                        + tagQuery + "&search=" + searchField);
            } else if (tagId != null && authorId != null) {
                getParameters(authorId, tagId, userQuery, tagQuery);
                model.addAttribute("filter", userQuery.toString() + tagQuery);
            } else if (tagId != null && searchField != null) {
                getParameters(tagId, tagQuery);
                model.addAttribute("filter", tagQuery + "&search" + searchField);
            } else if (tagId != null) {
                getParameters(tagId, tagQuery);
                model.addAttribute("filter", tagQuery);
            } else if (authorId != null && searchField != null) {
                for (String id : authorId) {
                    userQuery.add(id);
                }
                model.addAttribute("filter", userQuery + "&search" + searchField);
            } else {
                if (authorId != null) {
                    for (String id : authorId) {
                        userQuery.add(id);
                    }
                }
                model.addAttribute("filter", userQuery.toString());
            }
            paginatedPosts = postDataService.filteredPosts(authorId, tagId, searchField, pagination);
        } else if (startDate != null && endDate != null) {
            Pageable pageInfo = PageRequest.of(start / limit, limit);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            paginatedPosts = postService.getPostsByDatesBetweenOrdered(startDate, endDate, pageInfo);
            model.addAttribute("startIndex", start);
            model.addAttribute(LIMIT_PARAM, limit);
        } else if (searchField != null) {
            paginatedPosts = postDataService.searchedPosts(searchField, pagination);
        } else {
            paginatedPosts = postService.paginatedPosts(pagination);

        }
        model.addAttribute("posts", paginatedPosts);
        model.addAttribute("totalElements", paginatedPosts.getTotalElements());
        return "posts.html";
    }

    private void getParameters(@RequestParam(value = AUTHOR_ID, required = false) String[] authorId,
                               @RequestParam(value = TAG_ID, required = false) String[] tagId,
                               StringJoiner filteredQuery, StringJoiner tagQuery) {
        if (authorId != null) {
            for (String userId : authorId) {
                filteredQuery.add(userId);
            }
            for (String tag : tagId) {
                tagQuery.add(tag);
            }
        }
    }

    private void getParameters(@RequestParam(value = AUTHOR_ID, required = false) String[] ids, StringJoiner Query) {
        if (ids != null) {
            for (String userId : ids) {
                Query.add(userId);
            }
        }
    }

    @GetMapping("/create")
    public String createPage(Model model, Authentication authentication) {
        if (authentication != null) {
            model.addAttribute("post", new Post());
            User user = userService.getUserByEmail(authentication.getName());
            model.addAttribute("userName", user.getName());
            model.addAttribute("userId", user.getId());
            return "createpost.html";
        } else {
            return "error-404.html";
        }
    }

    @PostMapping("/create")
    public String createPost(@ModelAttribute Post post, Authentication authentication) {
        postService.createPost(post, authentication);
        return "redirect:/";
    }

    @GetMapping("/view")
    public String viewPost(@RequestParam("id") String id, Model model,
                           Authentication authentication,
                           @RequestParam(value = "commentId", required = false) String commentId) {
        Post post = postService.returnBlog(Long.parseLong(id));
        model.addAttribute("blog", post);
        User user;
        if (authentication != null) {
            String userName = authentication.getName();
            user=userService.getUserByEmail(userName);
            model.addAttribute("userDetails",user);
            if (post.getUser().getEmail().equals(userName) |
                    authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
                model.addAttribute("isAuthor", true);
            }
        }
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
    public String updatePost(@RequestParam("id") String id, Model model, Authentication authentication) {
        Post post = postService.returnBlog(Long.parseLong(id), authentication);
        System.out.println(post);
        if (post != null) {
            model.addAttribute("blog", post);
            model.addAttribute("id", id);
            return "update.html";
        } else {
            return "error-404.html";
        }
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute("blog") Post updatedPost) {
        postService.updatePost(updatedPost);
        return "redirect:/";
    }

    @GetMapping("/delete")
    public String deletePost(@RequestParam("id") long id, Authentication authentication) {
        return postService.deletePost(id, authentication) ? "redirect:/" : "error-404.html";
    }

    @PostMapping("/publish")
    public String publishPost(@ModelAttribute("blog") Post postToPublish, Authentication authentication) {
        postService.publishUpdatedPost(postToPublish, authentication);
        return "redirect:/draft?start=0";
    }

    @PostMapping("/publishnew")
    public String publishNewPost(@ModelAttribute("blog") Post postToPublish, Authentication authentication) {
        postService.publishPost(postToPublish, authentication);
        return "redirect:/";
    }
}
