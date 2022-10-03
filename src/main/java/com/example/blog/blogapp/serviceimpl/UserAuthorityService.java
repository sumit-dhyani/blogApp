package com.example.blog.blogapp.serviceimpl;

import com.example.blog.blogapp.entity.UserAuthority;
import com.example.blog.blogapp.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class UserAuthorityService {
    @Autowired
    AuthorityRepository authorRepo;
    public UserAuthority findByAuthorityName(String name){
       return authorRepo.findByAuthority(name);
    }
}
