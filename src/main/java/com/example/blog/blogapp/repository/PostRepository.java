package com.example.blog.blogapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.blog.blogapp.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

//	List<Post> findByTitleOrContentOrTagsOrAuthor(String keyword);

	Page<Post> findAllByIsPublishedTrue(Pageable paging);
	
	Page<Post> findAllByIsPublishedFalse(Pageable paging);
	
	Page<Post> findAllByOrderByPublishedAtAsc(Pageable paging);
	Page<Post> findAllByOrderByPublishedAtDesc(Pageable paging);
	List<Post> findByTitleContainingIgnoreCase(String title);
	
	@Query(value="select distinct p.* from post p join post_tags q on p.id=q.post_id where q.tag_id=(select t.id from tag t where lower(t.name)=lower(:r)) or lower(p.title) LIKE lower(concat('%',:r,'%')) or lower(p.content) Like lower(concat('%',:r,'%')) or lower(p.author) LIKE lower(concat('%',:r,'%')) or lower(p.excerpt) LIKE lower(concat('%',:r,'%'))",nativeQuery=true)
	Page<Post> findByMultipleFieldsIgnoreCaseIn(@Param("r") String name,Pageable paging);
	
	@Query(value="select p.* from post p join post_tags q on p.id=q.post_id where q.tag_id=:tagId and p.user_id=:userId",nativeQuery=true)
	Optional<Post> findPostIfUserHasThatTag(@Param("tagId") Long tagId,@Param("userId") Long userId);
}
