package com.atm.controller.api;

import com.atm.business.abstracts.TransactionsServices;
import com.atm.model.dtos.payloads.records.requests.TransactionsFiltersRequest;
import com.atm.model.dtos.payloads.records.responses.TransactionDto;
import com.atm.model.dtos.payloads.records.responses.UserAccountTransaction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/atm/api/transactions")
public class TransactionsApi {

    @Autowired
    @Qualifier("transactionsManager")
    private TransactionsServices transactionsManager;

    @GetMapping("/user")
    public ResponseEntity<?> transactionsByAccount(
            @RequestParam("slug") String accountSlug,
            @RequestParam("page") int page,
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam("sortOrder") String order,
            @RequestParam("sortBy") String sortBy
    ) {
        // Preparing key, value pares
        Map<String, Object> response = new HashMap<>();
        Page<TransactionDto> transactions =
                transactionsManager.findAllByAccount(
                        accountSlug, from, to, page, order, sortBy
                );
        // registering response vars
        response.put("totalPages", transactions.getTotalPages());
        response.put("totalElements", transactions.getTotalElements());
        response.put("transactions", transactions.getContent());
        response.put("currentPage", page);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<?> transactions(@ModelAttribute
                                          TransactionsFiltersRequest filters) {
        // Preparing key, value pares
        Map<String, Object> response = new HashMap<>();
        Page<UserAccountTransaction> loadedTransactions =
                transactionsManager.findAllFiltered(filters);
        // registering response vars
        response.put("totalPages", loadedTransactions.getTotalPages());
        response.put("totalElements", loadedTransactions.getTotalElements());
        response.put("transactions", loadedTransactions.getContent());
        response.put("currentPage", filters.page());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
