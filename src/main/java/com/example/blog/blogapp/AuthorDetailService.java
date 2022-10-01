package com.example.blog.blogapp;

import com.example.blog.blogapp.entity.User;
import com.example.blog.blogapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
@Service
public class AuthorDetailService implements UserDetailsService {
    @Autowired
    UserRepository userRepo;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User author=userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("user not found"));
        return new org.springframework.security.core.userdetails.User(author.getEmail(),author.getPassword(),new ArrayList<>());
    }


}
