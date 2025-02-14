package com.atm.controller;

import com.atm.business.abstracts.UserService;
import com.atm.business.concretes.MessageServices;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.dtos.UserDetailsDto;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/atm/user/profile")
@Log4j2
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private MessageServices messageServices;

    @PostMapping
    public String update(@Valid @ModelAttribute("user")
                             UserDetailsDto userDto,
                         BindingResult bindingResult
    , RedirectAttributes redirectAttributes,
                         Authentication auth) throws Exception {
        CustomUserDetailsDto userDetails =
                (CustomUserDetailsDto) auth.getPrincipal();
        String slug = userDetails.getUser().getSlug();
        if (bindingResult.hasErrors()) {
            return "layout/user/profile";
        }
        userDto.setEmail(userDetails.getUser().getEmail());
        String res = userService.update(userDto, slug);
        redirectAttributes.addFlashAttribute("success",
                res);
        return "redirect:/atm/profile";
    }
}
