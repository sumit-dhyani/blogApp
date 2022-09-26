package com.example.blog.blogapp.service;

import java.util.List;

import com.example.blog.blogapp.entity.User;

public interface UserService {
	public User getUserById(Long id);
	public List<User> getAllUsers();
}
