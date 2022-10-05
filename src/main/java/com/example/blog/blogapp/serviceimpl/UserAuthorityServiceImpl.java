package com.example.blog.blogapp.serviceimpl;

import com.example.blog.blogapp.entity.UserAuthority;
import com.example.blog.blogapp.repository.AuthorityRepository;
import com.example.blog.blogapp.service.UserAuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserAuthorityServiceImpl implements UserAuthorityService {

    AuthorityRepository authorRepo;
    @Autowired
    public UserAuthorityServiceImpl(AuthorityRepository authorRepo){
        this.authorRepo=authorRepo;
    }
    public UserAuthority findByAuthorityName(String name){
       Optional<UserAuthority> authority= authorRepo.findByAuthority(name);
       if(authority.isPresent()){
           return authority.get();
       }
       else{
           return new UserAuthority("AUTHOR");
       }

    }
}
