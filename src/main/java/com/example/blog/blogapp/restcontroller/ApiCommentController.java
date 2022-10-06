package com.example.blog.blogapp.restcontroller;

import com.example.blog.blogapp.entity.Comment;
import com.example.blog.blogapp.entity.User;
import com.example.blog.blogapp.repository.UserRepository;
import com.example.blog.blogapp.serviceimpl.CommentServiceImpl;
import com.example.blog.blogapp.serviceimpl.RestServiceImpl;
import com.example.blog.blogapp.serviceimpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/comment")
public class ApiCommentController {

    private final UserServiceImpl userService;
    private final CommentServiceImpl commentService;
    private final RestServiceImpl restService;
    @Autowired
    public ApiCommentController(UserServiceImpl userService, CommentServiceImpl commentService, RestServiceImpl restService){
        this.userService=userService;
        this.commentService=commentService;
        this.restService=restService;
    }
    @PostMapping("add/{id}")
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment,@PathVariable("id") long id, Authentication authentication){
        if(authentication!=null){
            User user=userService.getUserByEmail(authentication.getName());
            comment.setEmail(user.getEmail());
            comment.setName(user.getName());
        }
        commentService.createComment(comment,id,authentication);

        return new ResponseEntity<>(comment,HttpStatus.CREATED);
    }

    @PostMapping("delete/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable("id") long id, Authentication authentication){
        restService.deleteComment(id,authentication);
        return new ResponseEntity<>("Comment Deleted",HttpStatus.OK);
    }
}
