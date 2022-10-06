package com.example.blog.blogapp.repository;

import com.example.blog.blogapp.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> , JpaSpecificationExecutor<Post> {
    Page<Post> findAllByIsPublishedTrue(Pageable paging);
    @Query(value = "select p.* from post p join users u on p.user_id=u.id " +
            "where p.is_published=:attrib and u.email=:email",nativeQuery = true)
    Page<Post> findPostsUnpublishedByUser(@Param("attrib") boolean attrib,@Param("email") String email,Pageable paging);
    List<Post> findAll(Specification<Post> specification);
}
