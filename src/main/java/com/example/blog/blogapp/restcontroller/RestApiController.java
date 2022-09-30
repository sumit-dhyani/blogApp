package com.example.blog.blogapp.restcontroller;
import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RestApiController {

    PostRepository postRepo;
    @Autowired
    public RestApiController(PostRepository postRepo){
        this.postRepo=postRepo;
    }
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts(){
        return new ResponseEntity<List<Post>>(postRepo.findAll(),HttpStatus.OK);
    }
    @GetMapping("{id}")
    public Post getPostById(@PathVariable("id") long id){
        Post postFound=postRepo.findById(id).orElseThrow(()->new RuntimeException("not found"));
        return postFound;
    }

}
