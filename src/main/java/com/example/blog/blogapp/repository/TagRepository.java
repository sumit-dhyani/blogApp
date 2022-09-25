package com.example.blog.blogapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.blog.blogapp.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
	
	Optional<Tag> findByNameIgnoreCase(String name);
	
	Optional<Tag> findById(Long id);
	
	@Query(value="select distinct t.* from tag t join post_tags q on t.id=q.tag_id",nativeQuery=true)
	List<Tag> findAllLinkedTags();
}
