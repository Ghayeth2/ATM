package com.atm.controller.api;

import com.atm.business.abstracts.AccountServices;
import com.atm.core.utils.converter.DateFormat;
import com.atm.model.dtos.AccountDto;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.entities.Account;
import com.atm.model.entities.User;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/*
accounts.page.size
transactions.page.size
users.page.size
 */
@RestController @Log4j2 @AllArgsConstructor
@RequestMapping("/api/accounts")
public class AccountsApi {
    private AccountServices accountServices;
    private DateFormat dateFormat;

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam("slug") String slug) {

        accountServices.delete(slug);
        log.info("is account being deleted , is the function being called??!");
        return ResponseEntity.ok().build();
    }


    // Define a Map <String, Object> and return it
    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam("page") int page, Authentication auth,
                                     @RequestParam("searchQuery") String searchQuery,
                                     @RequestParam("sortBy") String sortBy,
                                     @RequestParam("order") String order,
                                     @RequestParam("from")String from,
                                     @RequestParam("to") String to) throws IOException, ParseException {
        CustomUserDetailsDto userDto = (CustomUserDetailsDto) auth.getPrincipal();
//        log.info("request : "+page+" "+searchQuery+" "+sortBy+" "+order+" "+from+" "+to);


        Map<String, Object> response = new HashMap<>();
        Page<AccountDto> accounts = accountServices.findAll(userDto.getUser().getId(),page, searchQuery,
                sortBy, order, from, to);
        log.info("total pages: " + accounts.getTotalPages());
        response.put("totalPages", accounts.getTotalPages());
        response.put("totalElements", accounts.getTotalElements());
        response.put("accounts", accounts.getContent());
        response.put("currentPage", page);
        return ResponseEntity.ok(response);
    }
}
