package com.example.blog.blogapp.model;

import com.example.blog.blogapp.entity.UserAuthority;

public class UserModel {
    private String email;
    private String password;

    private UserAuthority userAuthority;

    public UserAuthority getUserAuthority() {
        return userAuthority;
    }

    public void setUserAuthority(UserAuthority userAuthority) {
        this.userAuthority = userAuthority;
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

    public UserModel(String name, String password) {
        this.email = name;
        this.password = password;
    }
    public UserModel(){

    }

    @Override
    public String toString() {
        return "UserModel{" +
                "name='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
