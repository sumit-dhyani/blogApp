package com.example.blog.blogapp;

import com.example.blog.blogapp.entity.User;
import com.example.blog.blogapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class AuthorDetailService implements UserDetailsService {

    UserRepository userRepo;
    @Autowired
    public AuthorDetailService(UserRepository userRepo){
        this.userRepo=userRepo;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> author=userRepo.findByEmail(email);
        author.orElseThrow(()->new RuntimeException("user not found"));
        return author.map(MyUserDetails::new).get();
    }

}
