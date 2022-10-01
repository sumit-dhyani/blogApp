package com.example.blog.blogapp;

import com.example.blog.blogapp.entity.User;
import com.example.blog.blogapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserRepository userRepo;
    @Lazy
    @Autowired
    private PasswordEncoder passEncoder;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email =authentication.getName();
        String password=authentication.getCredentials().toString();
        User user=userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("user not found"));
        if(passEncoder.matches(password,user.getPassword())){
            return new UsernamePasswordAuthenticationToken(email,password,new ArrayList<>());
        }
        else{
            throw new BadCredentialsException("not correct password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
