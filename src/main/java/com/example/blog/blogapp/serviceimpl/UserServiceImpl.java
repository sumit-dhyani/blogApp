package com.example.blog.blogapp.serviceimpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.blog.blogapp.entity.User;
import com.example.blog.blogapp.repository.UserRepository;
import com.example.blog.blogapp.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	UserRepository userRepo;
	@Autowired
	UserServiceImpl(UserRepository userRepo){
		this.userRepo=userRepo;
	}

	@Override
	public User getUserById(Long id) {
		return userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found by id"+id));
	}

	public String getUserName(String email){
		User user=userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("user not present"));
		return user.getName();
	}
	@Override
	public List<User> getAllUsers() {
		return userRepo.findAll();
	}

	public User getUserByEmail(String email){
		return userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("User not present"));
	}

}
