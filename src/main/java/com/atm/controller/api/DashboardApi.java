package com.atm.controller.api;

import com.atm.business.abstracts.DashboardServices;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.dtos.payloads.records.requests.AccountFilters;
import com.atm.model.dtos.payloads.records.requests.TransactionFilters;
import com.atm.model.dtos.payloads.records.requests.UserFilters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/atm/api/dashboard")
@RequiredArgsConstructor
public class DashboardApi {
    private final DashboardServices dashboardServices;

    @GetMapping("/users")
    public ResponseEntity<?> users(@ModelAttribute UserFilters filters) {
        log.info("User filters: "+ filters.firstName()+ " "+ filters.lastName()
        + " "+ filters.from()+" "+ filters.to());
        Map<String, Long> response = new HashMap<>();
        response.put("usersCount", dashboardServices.users(filters));
        log.info("User filters: "+ response.get("usersCount"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/accounts")
    public ResponseEntity<?> accounts(@ModelAttribute AccountFilters filters,
                                      Authentication auth) {
        CustomUserDetailsDto userDetails =  (CustomUserDetailsDto) auth.getPrincipal();
        log.info("Account filters: " + filters.type() +" "+filters.email()+
                " "+filters.to()+" "+filters.from());
        Map<String, Long> response = new HashMap<>();
        response.put("accountsCount", dashboardServices.accounts(filters,
                userDetails));
        log.info("Accounts count: "+response.get("accountsCount"));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> transactions(@ModelAttribute TransactionFilters filters,
                                          Authentication auth) {
        log.info("Transaction filters: "+filters.email()+" "+filters.transactionType()
        +" "+filters.to()+" "+filters.from()+ " "+filters.accountNumber()+" "+filters.accountType());
        CustomUserDetailsDto userDetails = (CustomUserDetailsDto) auth.getPrincipal();
        Map<String, Long> response = new HashMap<>();
        response.put("transactionsCount", dashboardServices.transactions(filters,
                userDetails));
        log.info("Transactions count: "+response.get("transactionsCount"));
        return ResponseEntity.ok(response);
    }
}
