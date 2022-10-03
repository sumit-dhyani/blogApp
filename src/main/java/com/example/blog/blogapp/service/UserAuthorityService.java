package com.example.blog.blogapp.service;

import com.example.blog.blogapp.entity.UserAuthority;

public interface UserAuthorityService {
    public UserAuthority findByAuthorityName(String name);
}
