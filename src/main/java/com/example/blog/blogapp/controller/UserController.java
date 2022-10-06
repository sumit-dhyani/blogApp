package com.example.blog.blogapp.controller;

import com.example.blog.blogapp.entity.Post;
import com.example.blog.blogapp.entity.User;
import com.example.blog.blogapp.entity.UserAuthority;
import com.example.blog.blogapp.repository.UserRepository;
import com.example.blog.blogapp.serviceimpl.PostServiceImpl;
import com.example.blog.blogapp.serviceimpl.UserAuthorityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/")
public class UserController {

    private final PasswordEncoder bCrypt;
    private final PostServiceImpl postService;
    private final UserRepository userRepo;
    private final UserAuthorityServiceImpl authorityService;
    @Autowired
    public UserController(PasswordEncoder bCrypt, UserRepository userRepo,
                          UserAuthorityServiceImpl authorityService, PostServiceImpl postService){
        this.postService=postService;
        this.bCrypt=bCrypt;
        this.userRepo=userRepo;
        this.authorityService=authorityService;
    }
    @GetMapping("/register")
    public String userRegistration(Model model){
        model.addAttribute("userForm",new User());
        return "registration.html";
    }


    @PostMapping("/registered")
    public String registerUser(@ModelAttribute User author,Model model){
        Optional<User> user=userRepo.findByEmail(author.getEmail());
        if(user.isPresent()){
            model.addAttribute("userExists","User with same email already Exists");
            return "registration.html";
        }
        User authorDetails=new User();
        authorDetails.setName(author.getName());
        authorDetails.setEmail(author.getEmail());
        authorDetails.setPassword(bCrypt.encode(author.getPassword()));
        UserAuthority authority=authorityService.findByAuthorityName("AUTHOR");
        authorDetails.setUserAuthority(authority);
        userRepo.save(authorDetails);
        return "redirect:/";
    }


    @GetMapping("/draft")
    public String postNotPublished(@RequestParam("start") int start, Model model,
                                   @RequestParam(value = "limit", defaultValue = "4") int limit,
                                   Authentication authentication) {
        Page<Post> paginatedPosts = postService.getUnpublishedPost(false,start, limit, authentication);
        model.addAttribute("posts", paginatedPosts);
        model.addAttribute("totalElements", paginatedPosts.getTotalElements());
        model.addAttribute("startIndex", start);
        model.addAttribute("limit", 4);
        return "draft.html";
    }

    @GetMapping("/myPosts")
    public String myPosts(@RequestParam("start") int start, Model model,
                          @RequestParam(value = "limit", defaultValue = "4") int limit,
                          Authentication authentication){
        Page<Post> paginatedPosts=postService.getUnpublishedPost(true,start,limit,authentication);
        model.addAttribute("posts", paginatedPosts);
        model.addAttribute("totalElements", paginatedPosts.getTotalElements());
        model.addAttribute("startIndex", start);
        model.addAttribute("limit", 4);
        return "myposts.html";
    }

}
