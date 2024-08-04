package com.atm.controller;

import com.atm.business.abstracts.UserAccount;
import com.atm.business.abstracts.UserService;
import com.atm.core.utils.converter.DtoEntityConverter;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.dtos.UserDetailsDto;
import com.atm.model.dtos.UserDto;
import com.atm.model.entities.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/atm") @Log4j2
public class MainController {
    @Autowired
    private UserService userService;
    @Autowired
    private DtoEntityConverter converter;

    // User side home page
    @GetMapping
    public String index(){
        return "layout/home";
    }
    // Login page
    @GetMapping("/login")
    public String login(){
        return "layout/login";
    }

    @GetMapping("/email_confirmed")
    public String emailConfirmed(){
        return "layout/email_confirmed";
    }

    @GetMapping("/password/forgot")
    public String forgot(){
        return "layout/email_resetpass";
    }

    // Registration page
    @GetMapping("/registration")
    public String signup(Model model){
        model.addAttribute("user", new UserDto());
        return "layout/signup";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication auth){
        CustomUserDetailsDto userDetails =  (CustomUserDetailsDto) auth.getPrincipal();
        User userModel = userDetails.getUser();
        UserDetailsDto user = (UserDetailsDto) converter.entityToDto(userModel, new UserDetailsDto());

        log.info("Logged in user slug model"+ userDetails.getUser().getSlug());
//        log.info("Logged in user slug data"+ user.getSlug());
        model.addAttribute("user", user);
        return "layout/profile";
    }

    // Admin side home page
}
