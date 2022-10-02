package com.example.blog.blogapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.blog.blogapp.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
//    @Query(value="select u.* from users u where email=:provided",nativeQuery = true)
    public Optional<User> findByEmail(String provided);
}
