package com.example.blog.blogapp.repository;

import com.example.blog.blogapp.entity.UserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository  extends JpaRepository<UserAuthority,Long> {
    public UserAuthority findByAuthority(String name);
}
