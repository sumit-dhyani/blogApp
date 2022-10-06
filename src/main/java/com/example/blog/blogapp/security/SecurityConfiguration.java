package com.example.blog.blogapp.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests().
                antMatchers("/","/view","/register","/registered",
                        "/login","/loginSubmit","/comment/**").
                permitAll().
                antMatchers("/draft","/publish","/publishnew","/delete",
                        "/update","/api/delete/","api/add","api/update")
                .hasAnyAuthority("AUTHOR","ADMIN").
                and().formLogin().permitAll().and().logout().logoutSuccessUrl("/").permitAll().and().
                exceptionHandling().accessDeniedPage("/error-404.html").and().
                httpBasic(withDefaults());
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
