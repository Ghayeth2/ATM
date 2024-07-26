package com.atm.controller;

import com.atm.business.abstracts.UserService;
import com.atm.business.concretes.MessageServices;
import com.atm.core.exceptions.EmailExistsException;
import com.atm.model.dtos.UserDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/atm/user")
@Log4j2
@AllArgsConstructor
public class AuthController {

    private MessageServices messageServices;
    private UserService userService;

    @PostMapping
    public String save(@Valid @ModelAttribute("user") UserDto userDto
                , BindingResult bindingResult, Model model
                        , RedirectAttributes redirectAttributes) throws EmailExistsException {

        if(!userDto.getPassword().equals(userDto.getPassword2()))
            return "redirect:/atm/registration?notMatched";
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userDto);
            log.error("An error occurred!");
            return "layout/signup";
        }
//        log.info("well is it being called??");
        log.info("Success " + userDto);
        userService.save(userDto);
        redirectAttributes.addFlashAttribute("success",
                messageServices.getMessage("scs.user.signup"));
        return "redirect:/atm/registration";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam("token") String token)  {
       return userService.confirmToken(token);
//        return "redirect:/atm/login";
    }
}
