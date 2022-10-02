package com.example.blog.blogapp.controller;

import com.example.blog.blogapp.entity.Comment;
import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.entity.User;
import com.example.blog.blogapp.repository.PostRepository;
import com.example.blog.blogapp.serviceimpl.CommentServiceImpl;
import com.example.blog.blogapp.serviceimpl.PostServiceImpl;
import com.example.blog.blogapp.serviceimpl.TagServiceImpl;
import com.example.blog.blogapp.serviceimpl.UserServiceImpl;
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

import java.text.ParseException;
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
    private final CommentServiceImpl commentService;
    private final UserServiceImpl userService;
    @Autowired
    PostRepository postRepository;

    @Autowired
    PostController(PostServiceImpl postService, TagServiceImpl tagService, CommentServiceImpl commentService, UserServiceImpl userService) {
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
                              @RequestParam(value = END_DATE, required = false) String endDate) throws ParseException {
        model.addAttribute(SEARCH, searchField);
        model.addAttribute("tagNames", tagService.getLinkedTags());
        model.addAttribute("userNames", userService.getAllUsers());
        model.addAttribute(ORDER, order);
        model.addAttribute("startIndex", start);
        model.addAttribute(LIMIT_PARAM, limit);
        Page<Post> paginatedPosts;
        if (order != null) {
            StringJoiner userQuery = new StringJoiner("&authorId=", "&authorId=", "");
            StringJoiner tagQuery = new StringJoiner("&tagId=", "&tagId=", "");
            if (authorId != null && tagId != null) {
                getParameters(authorId, tagId, userQuery, tagQuery);
                model.addAttribute("filter", userQuery.toString() + tagQuery);
                Pageable pagination;
                if (order.equals("asc")) {
                    pagination = PageRequest.of(start / limit, limit, Sort.by("published_at").ascending());
                } else {
                    pagination = PageRequest.of(start / limit, limit, Sort.by("published_at").descending());
                }
                paginatedPosts = postService.getPostsByUserAndTagIdSorted(tagId, authorId, order, pagination);
            } else if (startDate != null) {
                model.addAttribute("startDate", startDate);
                model.addAttribute("endDate", endDate);
                Pageable pagination;
                if (order.equals("asc")) {
                    pagination = PageRequest.of(start / limit, limit, Sort.by("published_at").ascending());
                } else {
                    pagination = PageRequest.of(start / limit, limit, Sort.by("published_at").descending());
                }
                paginatedPosts = postService.getPostsByDatesBetweenOrdered(startDate, endDate, pagination);
            } else if (authorId != null) {
                getParameters(authorId, userQuery);
                model.addAttribute("filter", userQuery.toString());
                paginatedPosts = postService.getPostsByAuthorSorted(start, limit, order, authorId);
            } else if (tagId != null) {
                getParameters(tagId, tagQuery);
                model.addAttribute("filter", tagQuery.toString());
                paginatedPosts = postService.getPostsByTagIdSorted(start, limit, order, tagId);
            } else {
                Pageable pagination = PageRequest.of(start / limit, limit);
                paginatedPosts = postService.findAllByOrderByPublished(order, pagination);
            }
        } else if (authorId != null | tagId != null) {
            StringJoiner userQuery = new StringJoiner("&authorId=", "&authorId=", "");
            StringJoiner tagQuery = new StringJoiner("&tagId=", "&tagId=", "");
            Set<Post> filteredPosts = new HashSet<>();
            StringJoiner dateQuery = new StringJoiner("&endDate", "&startDate", "");
            if (tagId != null && authorId != null && searchField != null) {
                List<Post> searchedPosts = postService.getSearchedPosts(searchField);
                Set<Post> filtered = new HashSet<>();
                getParameters(authorId, tagId, userQuery, tagQuery);
                filtered = postService.getPostsByUserAndTagId(tagId, authorId);
                for (Post ifResultsIncludeSearchItems : searchedPosts) {
                    if (filtered.contains(ifResultsIncludeSearchItems)) {
                        filteredPosts.add(ifResultsIncludeSearchItems);
                    }
                }
                model.addAttribute("filter", userQuery.toString() + tagQuery + "&search=" + searchField);
            } else if (tagId != null && authorId != null) {
                getParameters(authorId, tagId, userQuery, tagQuery);
                filteredPosts = postService.getPostsByUserAndTagId(tagId, authorId);
                model.addAttribute("filter", userQuery.toString() + tagQuery);
            } else if (tagId != null && searchField != null) {
                getParameters(tagId, tagQuery);
                List<Post> searchedPosts = postService.getSearchedPosts(searchField);
                Set<Post> filtered = postService.getPostsByTagId(tagId);
                for (Post ifResultsIncludeSearchItems : searchedPosts) {
                    if (filtered.contains(ifResultsIncludeSearchItems)) {
                        filteredPosts.add(ifResultsIncludeSearchItems);
                    }
                }
                model.addAttribute("filter", tagQuery + "&search" + searchField);
            } else if (tagId != null) {
                getParameters(tagId, tagQuery);
                filteredPosts = postService.getPostsByTagId(tagId);
                model.addAttribute("filter", tagQuery);
            } else if (authorId != null && searchField != null) {
                List<Post> searchedPosts = postService.getSearchedPosts(searchField);
                Set<Post> filtered = new HashSet<>();
                for (String id : authorId) {
                    userQuery.add(id);
                    User author = userService.getUserById(Long.parseLong(id));
                    filtered.addAll(author.getPostsByUser());
                }
                for (Post ifResultsIncludeSearchItems : searchedPosts) {
                    if (filtered.contains(ifResultsIncludeSearchItems)) {
                        filteredPosts.add(ifResultsIncludeSearchItems);
                    }
                }
                model.addAttribute("filter", userQuery + "&search" + searchField);
            } else {
                for (String id : authorId) {
                    userQuery.add(id);
                    User author = userService.getUserById(Long.parseLong(id));
                    filteredPosts.addAll(author.getPostsByUser());
                }
                model.addAttribute("filter", userQuery.toString());
            }
            List<Long> filteredPostIds = new ArrayList<>();
            for (Post posts : filteredPosts) {
                filteredPostIds.add(posts.getId());
            }
            Pageable pagination = PageRequest.of(start / limit, limit);
            paginatedPosts = postService.getPaginatedItems(filteredPostIds, pagination);
        } else if (startDate != null && endDate != null) {
            Pageable pageInfo = PageRequest.of(start / limit, limit);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            paginatedPosts = postRepository.findAllPostsByPublishedAtBetween(startDate, endDate, pageInfo);
        } else if (searchField != null) {
            Pageable pagination = PageRequest.of(start / limit, limit);
            paginatedPosts = postService.getSearchedPosts(searchField, pagination);
        } else {
            Pageable pagination = PageRequest.of(start / limit, limit);
            paginatedPosts = postService.paginatedPosts(pagination);

        }
        model.addAttribute("posts", paginatedPosts);
        model.addAttribute("totalElements", paginatedPosts.getTotalElements());
        return "posts.html";
    }

    private void getParameters(@RequestParam(value = AUTHOR_ID, required = false) String[] authorId, @RequestParam(value = TAG_ID, required = false) String[] tagId, StringJoiner filteredQuery, StringJoiner tagQuery) {
        for (String userId : authorId) {
            filteredQuery.add(userId);
        }
        for (String tag : tagId) {
            tagQuery.add(tag);
        }
    }

    private void getParameters(@RequestParam(value = AUTHOR_ID, required = false) String[] ids, StringJoiner Query) {
        for (String userId : ids) {
            Query.add(userId);
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
    public String updatePost(@RequestParam("id") String id, Model model,Authentication authentication) {
        Post post=postService.returnBlog(Long.parseLong(id),authentication);
        System.out.println(post);
        if(post!=null){
            model.addAttribute("blog", post);
            model.addAttribute("id", id);
            return "update.html";
        }
        else{
            return "redirect:/";
        }
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute("blog") Post updatedPost) {
        postService.updatePost(updatedPost);
        return "redirect:/";
    }

    @GetMapping("/delete")
    public String deletePost(@RequestParam("id") long id,Authentication authentication) {
        return postService.deletePost(id,authentication)?"redirect:/":"/error.html";
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
