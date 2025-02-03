package com.atm.controller;

import com.atm.business.abstracts.AccountServices;
import com.atm.business.abstracts.UserService;
import com.atm.core.utils.converter.DtoEntityConverter;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.dtos.UserDetailsDto;
import com.atm.model.dtos.UserDto;
import com.atm.model.dtos.payloads.requests.TransactionRequest;
import com.atm.model.entities.Account;
import com.atm.model.entities.User;
import com.atm.model.enums.Currencies;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;

/**
 * MainController class is not handling
 * any SRP at all, needs to be fixed
 * and isolating each service alone
 * u might call it PublicController
 * to render public views.
 */

@Controller
@RequestMapping("/atm")
@Log4j2
@AllArgsConstructor
public class MainController {

    private DtoEntityConverter converter;
    private AccountServices accountServices;

    @GetMapping("/transfer")
    public String transfer(Model model,
                           @RequestParam(value = "slug",
                                   required = false) String slug) {
        if (slug != null) {
            Account account = accountServices.findBySlug(slug);
            TransactionRequest request = TransactionRequest.builder()
                    .receiverNumber(account.getNumber()).build();
            model.addAttribute("transaction", request);
        }
        return "layout/transactions/transfer";
    }

    @GetMapping("/user/transactions")
    public String userTransactions(Model model,
                                   @RequestParam("slug")
                                   String slug) {
        model.addAttribute("slug", slug);
        return "layout/transactions/index";
    }

    @GetMapping("/withdraw")
    public String withdraw(Model model,
                           @RequestParam(value = "slug",
                                   required = false) String slug) {
        if (slug != null) {
            Account account = accountServices.findBySlug(slug);
            TransactionRequest request = TransactionRequest.builder()
                    .receiverNumber(account.getNumber()).build();
            model.addAttribute("transaction", request);
        }
        return "layout/transactions/withdraw";
    }

    @GetMapping("/deposit")
    public String deposit(Model model,
                          @RequestParam(value = "slug",
                                  required = false) String slug) {
        if (slug != null) {
            Account account = accountServices.findBySlug(slug);
            TransactionRequest request = TransactionRequest.builder()
                    .receiverNumber(account.getNumber()).build();
            model.addAttribute("transaction", request);
        }
        return "layout/transactions/deposit";
    }

    // User side home page
    @GetMapping
    public String index() {
        return "layout/home";
    }

    // Login page
    @GetMapping("/login")
    public String login() {
        return "layout/auth/login";
    }

    @GetMapping("/admin/transactions")
    public String transactions(Model model) {
        return "layout/admin/transactions";
    }

    @GetMapping("/admin/settings")
    public String settings(Model model) {
        return "layout/admin/settings";
    }

    @GetMapping("/email_confirmed")
    public String emailConfirmed(Model model) {
        if (!model.containsAttribute("illegal")
                || !model.containsAttribute("message")) {
            model.addAttribute("illegal", false);
            model.addAttribute("message", "");
        }
        return "layout/auth/email_confirmed";
    }

    @GetMapping("/password/forgot")
    public String forgot() {
        return "layout/auth/email_resetpass";
    }

    @GetMapping("/reset/password")
    public String resetPassword() {
        return "layout/auth/resetPassword";
    }

    // Registration page
    @GetMapping("/registration")
    public String signup(Model model) {
        model.addAttribute("user", new UserDto());
        return "layout/auth/signup";
    }

    /*
    if i was using renders, handlers, api
    structure. it wouldn't be violating anything
    in renders i render pages with/without data
    in here now main controller is already violating SRP
    and could not fix it. So for this project continue with
    AccountController. for all account related services.
     */
//    @GetMapping("/accounts")
//    public String accounts(Model model){}

    // if rename the controller to public. only this violates it
    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
        CustomUserDetailsDto userDetails = (CustomUserDetailsDto) auth.getPrincipal();
        User userModel = userDetails.getUser();
        UserDetailsDto user = (UserDetailsDto) converter.entityToDto(userModel, new UserDetailsDto());

        log.info("Logged in user slug model" + userDetails.getUser().getSlug());
//        log.info("Logged in user slug data"+ user.getSlug());
        model.addAttribute("user", user);
        return "layout/user/profile";
    }

    // Admin side home page
}
