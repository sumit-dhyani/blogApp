package com.example.blog.blogapp.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.blog.blogapp.entity.Post;

@Repository
public interface BlogRepository extends JpaRepository<Post, Long> {

//	List<Post> findByTitleOrContentOrTagsOrAuthor(String keyword);

	Page<Post> findAll(Pageable paging);
	
	List<Post> findByTitleContainingIgnoreCase(String title);
	
	@Query(value="select distinct p.* from post p join post_tags q on p.id=q.post_id where q.tag_id=(select t.id from tag t where lower(t.name)=lower(:r)) or lower(p.title) LIKE lower(concat('%',:r,'%')) or lower(p.content) Like lower(concat('%',:r,'%')) or lower(p.author) LIKE lower(concat('%',:r,'%')) or lower(p.excerpt) LIKE lower(concat('%',:r,'%'))",nativeQuery=true)
	List<Post> findByMultipleFieldsIgnoreCaseIn(@Param("r") String name);
}
