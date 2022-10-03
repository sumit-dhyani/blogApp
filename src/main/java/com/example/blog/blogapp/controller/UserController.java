package com.example.blog.blogapp.controller;

import com.example.blog.blogapp.entity.User;
import com.example.blog.blogapp.entity.UserAuthority;
import com.example.blog.blogapp.model.UserModel;
import com.example.blog.blogapp.repository.UserRepository;
import com.example.blog.blogapp.serviceimpl.UserAuthorityServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class UserController {
    @Autowired
    private PasswordEncoder bCrypt;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserAuthorityServiceImpl authorityService;

    @GetMapping("/login")
    public String showLoginPage() {

        return "login.html";
    }
    @RequestMapping("/login-fail")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);

        return "redirect:/login";
    }

    @PostMapping("/loginSubmit")
    public String submitLoginDetails(@ModelAttribute UserModel userModel) throws Exception {

        Authentication authentication;
        try{
            authentication=authManager.
                    authenticate(new UsernamePasswordAuthenticationToken
                            (userModel.getEmail(),userModel.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (BadCredentialsException e){
            return "redirect:/login";
        }
        return "redirect:/";
    }

    @GetMapping("/register")
    public String userRegistration(Model model){
        model.addAttribute("userForm",new User());
        return "/registration.html";
    }


    @PostMapping("/registered")
    public String registerUser(@ModelAttribute User author){
        User authorDetails=new User();
        authorDetails.setName(author.getName());
        authorDetails.setEmail(author.getEmail());
        authorDetails.setPassword(bCrypt.encode(author.getPassword()));
        UserAuthority authority=authorityService.findByAuthorityName("AUTHOR");
        authorDetails.setUserAuthority(authority);
        userRepo.save(authorDetails);
        return "redirect:/";
    }



}
