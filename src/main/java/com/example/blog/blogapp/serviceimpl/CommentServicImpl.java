package com.example.blog.blogapp.serviceimpl;

import com.example.blog.blogapp.entity.Comment;
import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.repository.CommentRepository;
import com.example.blog.blogapp.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentServicImpl implements CommentService {

    private final PostServiceImpl postService;
    private final CommentRepository commentRepo;
	@Autowired
	public CommentServicImpl(PostServiceImpl postService,CommentRepository commentRepo){
		this.commentRepo=commentRepo;
		this.postService =postService;
	}
    @Override
    public void createComment(Comment comment, long id) {
        Post existingPost = postService.returnBlog(id);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(existingPost);
        commentRepo.save(comment);
    }

    public void updateComment(Comment updatedComment, long id) {
        Comment comment = returnComment(id);
        comment.setComment(updatedComment.getComment());
        comment.setName(updatedComment.getName());
        comment.setEmail(updatedComment.getEmail());
        commentRepo.save(comment);
    }

    public String deleteComment(long id) {
        Comment comment = commentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        Long postId = comment.getPost().getId();
        commentRepo.deleteById(id);
        return postId.toString();
    }

    public Comment returnComment(long id) {
        return commentRepo.findById(id).orElseThrow(() -> new RuntimeException("Comment Not Found"));
    }
}
