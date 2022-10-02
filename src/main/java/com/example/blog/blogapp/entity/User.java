package com.example.blog.blogapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;

import javax.persistence.*;


@Entity
@Table(name="users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	private String email;
	@JsonIgnore
	private String password;

	@OneToOne
	private UserAuthority userAuthority;

	public UserAuthority getUserAuthority() {
		return userAuthority;
	}

	public void setUserAuthority(UserAuthority userAuthority) {
		this.userAuthority = userAuthority;
	}

	@OneToMany(mappedBy="user",cascade = {CascadeType.ALL})
	private List<Post> postsByUser=new ArrayList<>();
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public List<Post> getPostsByUser() {
		return postsByUser;
	}
	public void setPostsByUser(List<Post> postsByUser) {
		this.postsByUser = postsByUser;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public User(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}
	public User() {
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", name='" + name + '\'' +
				", email='" + email + '\'' +
				", password='" + password + '\'' +
				'}';
	}
}
