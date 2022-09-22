package com.example.blog.blogapp.entity;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.*;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;


@Entity
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	@Column(name="created_at")
	private LocalDateTime createdAt;
	@ManyToMany(mappedBy = "tags",cascade= CascadeType.ALL)	
	private List<Post> postTag=new ArrayList<>();
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public List<Post> getPostTag() {
		return postTag;
	}
	public void setPostTag(List<Post> postTag) {
		this.postTag = postTag;
	}
	public Tag(String name, LocalDateTime createdAt) {
		this.name = name;
		this.createdAt = createdAt;
	}
	public Tag() {
	}
	@Override
	public String toString() {
		return "Tag [id=" + id + ", name=" + name + ", createdAt=" + createdAt + "]";
	}
	
	
	
	
}
