package com.atm.controller;

import com.atm.business.abstracts.TransactionsServices;
import com.atm.model.dtos.payloads.requests.TransactionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/atm/transactions")

public class TransactionController {

    /*
    I don't why it happened, but this is how i solved this problem.
     */
    @Autowired
    @Qualifier("transactionsManager")
    private TransactionsServices transactionServices;

    @PostMapping
    public String newTransaction( @Valid @ModelAttribute("transaction")
                                     TransactionRequest request,
                                 BindingResult bindingResult,
                                 Model model, RedirectAttributes redirectAttributes) {
        // Handle the form's errors & return to user
//        bindingResult.getAllErrors().forEach(error -> {
//            System.out.println("Validation Error: " + error.getDefaultMessage());
//        });
        System.out.println("senderNumber length: " + request.getSenderNumber()
                .length());
        if (bindingResult.hasErrors()) {
            model.addAttribute("transaction", request);
            return "layout/transactions/new";
        }
        // Calling transaction services
        String[] nubmers = {request.getSenderNumber()
        , request.getReceiverNumber()};
        String response = transactionServices.newTransaction(
                request.getType(), request.getAmount(),
                nubmers
        );
        // Redirecting to transaction template with response
        redirectAttributes.addFlashAttribute("response", response);
        return "redirect:/atm/transactions/new";
    }
}
