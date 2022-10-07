package com.example.blog.blogapp.serviceimpl;

import com.example.blog.blogapp.entity.Comment;
import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.entity.User;
import com.example.blog.blogapp.exceptions.ResourceNotFoundException;
import com.example.blog.blogapp.repository.CommentRepository;
import com.example.blog.blogapp.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {

    private final PostServiceImpl postService;
    private final CommentRepository commentRepo;
    private final UserServiceImpl userService;
	@Autowired
	public CommentServiceImpl(PostServiceImpl postService, CommentRepository commentRepo,UserServiceImpl userService){
		this.commentRepo=commentRepo;
		this.postService =postService;
        this.userService=userService;
	}
    @Override
    public void createComment(Comment comment, long id, Authentication authentication) {
        Post existingPost = postService.returnBlog(id);
        User user;
        if(authentication!=null) {
            user = userService.getUserByEmail(authentication.getName());
            comment.setName(user.getName());
            comment.setEmail(user.getEmail());
        }
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(existingPost);
        commentRepo.save(comment);
    }

    public void updateComment(Comment updatedComment, long id) {
        Comment comment = returnComment(id);
        comment.setComment(updatedComment.getComment());
        if(!comment.getName().equals(updatedComment.getName())) {
            comment.setName(updatedComment.getName());
            comment.setEmail(updatedComment.getEmail());
        }
        commentRepo.save(comment);
    }

    public String deleteComment(long id) {
        Comment comment = commentRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        Long postId = comment.getPost().getId();
        commentRepo.deleteById(id);
        return postId.toString();
    }

    public Comment returnComment(long id) {
        return commentRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Comment Not Found "));
    }
}
