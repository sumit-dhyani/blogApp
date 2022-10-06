package com.example.blog.blogapp.serviceimpl;

import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.entity.Tag;
import com.example.blog.blogapp.entity.User;
import com.example.blog.blogapp.exceptions.ResourceNotFoundException;
import com.example.blog.blogapp.exceptions.UnauthorizedException;
import com.example.blog.blogapp.repository.PostRepository;
import com.example.blog.blogapp.repository.TagRepository;
import com.example.blog.blogapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RestServiceImpl {

    private final UserRepository userRepo;
    private final TagRepository tagRepo;
    private final PostRepository postRepo;
    private final CommentServiceImpl commentService;

    @Autowired
    public RestServiceImpl(UserRepository userRepo, PostRepository postRepo, TagRepository tagRepo,CommentServiceImpl commentService){
        this.userRepo=userRepo;
        this.tagRepo=tagRepo;
        this.postRepo=postRepo;
        this.commentService=commentService;
    }

    public Post createPost(Post newPost, Authentication authentication) {

        User user=userRepo.findByEmail(authentication.getName())
                .orElseThrow(()->new RuntimeException("user not found"));
        newPost.setAuthor(user.getName());
        newPost.setCreatedAt(LocalDateTime.now());
        newPost.setIsPublished(true);
        newPost.setPublishedAt(LocalDateTime.now());
        if (newPost.getContent().length() > 100) {
            newPost.setExcerpt(newPost.getContent().substring(0, 100));
        } else {
            newPost.setExcerpt(newPost.getContent());
        }
        newPost.setUser(user);
        addTags(newPost, newPost);
        return postRepo.save(newPost);

    }
    public void deleteComment(long id, Authentication authentication){
        String userEmail=authentication.getName();
        String authorEmail=commentService.returnComment(id).getPost().getUser().getEmail();
        if (userEmail.equals(authorEmail)|
                authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            commentService.deleteComment(id);
        }
        else{
            throw new UnauthorizedException("Not Authorized");
        }
    }
    private void addTags(Post newPost, Post postToUpdate) {
        String[] tagList = newPost.getTagField().split(",");
        for (String tag : tagList) {
            Optional<Tag> existingTag = tagRepo.findByNameIgnoreCase(tag);
            if (existingTag.isPresent()) {
                postToUpdate.getTags().add(existingTag.get());
            } else {
                Tag tags = new Tag(tag, LocalDateTime.now());
                tags.getPostTag().add(postToUpdate);
                postToUpdate.getTags().add(tags);
            }
        }
    }

    public void deleteById(long id,Authentication authentication) {
        Post post=postRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Post not found"));
        if(authentication.getName().equals(post.getUser().getEmail())|
                authentication.getAuthorities().equals(new SimpleGrantedAuthority("AUTHOR"))){
            postRepo.deleteById(id);
        }
        else{
            throw new UnauthorizedException("You are not authorized to delete this post");
        }
    }
}
