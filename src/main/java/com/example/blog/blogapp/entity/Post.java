package com.example.blog.blogapp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
    private Long id;
	@Column(name = "title")
    private String title;
	@Column(name = "tags")
    private String tags;
	private String excerpt;
	@Column(name = "content")
    private String content;
	@Column(name = "author")
    private String author;
	@Column(name = "published_at")
    private Date publishedAt;
	@Column(name = "is_published")
    private Boolean isPublished;
	@Column(name = "created_at")
    private LocalDateTime createdAt;
	@Column(name = "updated_at")
    private LocalDateTime updatedAt;
	@OneToMany(mappedBy = "comment")
	private List<Comment> comments;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String excerpt) {
		this.tags = excerpt;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Date getPublishedAt() {
		return publishedAt;
	}
	public void setPublishedAt(Date publishedAt) {
		this.publishedAt = publishedAt;
	}
	public Boolean getIsPublished() {
		return isPublished;
	}
	public void setIsPublished(Boolean isPublished) {	
		this.isPublished = isPublished;
	}
	public LocalDate getCreatedAt() {
		return createdAt.toLocalDate();
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	public Post(String title, String excerpt, String content, String author, Date publishedAt, Boolean isPublished,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.title = title;
		this.tags = excerpt;
		this.content = content;
		this.author = author;
		this.publishedAt = publishedAt;
		this.isPublished = isPublished;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	public Post(String title, String content, String author) {
		this.title = title;
		this.content = content;
		this.author = author;
	}

	public Post() {
		
	}
	public String getExcerpt() {
		return excerpt;
	}
	public void setExcerpt(String excerpt) {
		this.excerpt = excerpt;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}



}
