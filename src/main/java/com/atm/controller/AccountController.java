package com.atm.controller;

import com.atm.business.abstracts.AccountServices;
import com.atm.business.concretes.MessageServices;
import com.atm.model.dtos.CustomUserDetailsDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/atm/accounts")
@AllArgsConstructor
@Log4j2
public class AccountController {
    private AccountServices accountServices;
    private MessageServices messageServices;

//    @PostMapping
    @GetMapping("/new")
    public String newAccount() {
        return "layout/accounts/new";
    }

    @GetMapping
    public String index(){
        return "layout/accounts/index";
    }

    @PostMapping
    public String saveAccount(@RequestParam("type") String type,
                              @RequestParam("currency") String currency,
                              Model model, Authentication auth) throws IOException {
        // Will never enter since i controlled the <select> tag with Enumeration.
        if (type.isEmpty() || currency.isEmpty()) {
            model.addAttribute("error", messageServices.getMessage("err.accounts.type"));
            return "layout/accounts/new";
        }
        accountServices.save(type, currency, ((CustomUserDetailsDto)auth.getPrincipal()).getUser());
        // Replacing the param inside the successes.properties file to display dynamic according to selected account type
//        configService.replaceMsgParameter("type", accountType, "scs.accounts");
        model.addAttribute("success", messageServices.getMessage("scs.accounts"));
        return "layout/accounts/new";
    }

}
