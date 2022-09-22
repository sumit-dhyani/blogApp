package com.example.blog.blogapp.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.blog.blogapp.repository.BlogRepository;

@Entity
public class Post {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
    private Long id;
	@Column(name = "title")
    private String title;
	@Column(name = "tag_field")
	private String tagField;
	private String excerpt;
	@Column(length = 1500)
    private String content;
	@Column(name = "author")
    private String author;
	@Column(name = "published_at")
    private Date publishedAt;
	public String getTagField() {
		return tagField;
	}
	public void setTagField(String tagField) {
		this.tagField = tagField;
	}
	public List<Tag> getTags() {
		return tags;
	}
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	@Column(name = "is_published")
    private Boolean isPublished;
	@Column(name = "created_at")
    private LocalDateTime createdAt;
	@Column(name = "updated_at")
    private LocalDateTime updatedAt;
	@OneToMany(mappedBy = "post",cascade = CascadeType.ALL)
	private Set<Comment> comments=new TreeSet<>();
//	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE,
//			 CascadeType.DETACH, CascadeType.REFRESH})
//	private User user;
    @ManyToMany(cascade= CascadeType.ALL)
    @JoinTable(name="post_tags",
	joinColumns = { @JoinColumn (name ="post_id")},
	inverseJoinColumns = { @JoinColumn (name= "tag_id")})
	private List<Tag> tags=new ArrayList<>();
//	
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
		System.out.println("id:"+this.id);
	}
	public String getExcerpt() {
		return excerpt;
	}
	public void setExcerpt(String excerpt) {
		this.excerpt = excerpt;
	}
	public Set<Comment> getComments() {
		return comments;
	}
	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}
	@Override
	public String toString() {
		return "Post [id=" + id + ", title=" + title  + ", excerpt=" + excerpt + ", content="
				+ content + ", author=" + author + ", publishedAt=" + publishedAt + ", isPublished=" + isPublished
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", comments=" + comments + "]";
	}
	
	
}
