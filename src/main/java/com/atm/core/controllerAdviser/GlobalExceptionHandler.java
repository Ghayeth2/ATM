package com.atm.core.controllerAdviser;

import com.atm.business.concretes.MessageServices;
import com.atm.core.exceptions.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.NoSuchElementException;

@AllArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    private MessageServices messageServices;

    @ExceptionHandler(EmailExistsException.class)
    public String handleEmailExistsException(EmailExistsException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exception", ex.getMessage());
        return "redirect:/atm/registration";
    }

    // this is another way to handle global exceptions dynamically with properties file
    // send redirect error attribute instead of parameter where it will get printed out
    // in case of exception occur, without using th:text="#{err.mismatch}" only
    // th:text="#{mismatch}" as an attribute not parameter so won't be like this > BASE_URL+"mismatch"
    // instead the url will remain same with no addition in end of it, and flush attribute will be sent
    @ExceptionHandler(PasswordMisMatchException.class)
    public String handleEmailExistsException(PasswordMisMatchException ex, RedirectAttributes redirectAttributes) {
        // Password mismatching attribute to get the message in frontend
        redirectAttributes.addFlashAttribute("misMatch", ex.getMessage());
        return "redirect:/atm/profile";
    }

    @ExceptionHandler(AccountInactiveException.class)

    public String handleAccountInactiveException(AccountInactiveException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("illegal", true);
        redirectAttributes.addFlashAttribute("message", ex.getMessage());
        return "redirect:/atm/email_confirmed";
    }

    /**
     * Handler for InsufficientFundsException
     * @param ex
     * @param redirectAttributes
     * @return
     */
    @ExceptionHandler(InsufficientFundsException.class)
    public String handleInsufficientFundsException(InsufficientFundsException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exception", ex.getMessage());
        return "redirect:/atm/transactions/new";
    }

    /**
     * Handler for AccountsCurrenciesMismatchException during transfer transaction
     * if the currencies of the two accounts differ.
     * @param ex
     * @param redirectAttributes
     * @return
     */
    @ExceptionHandler(AccountsCurrenciesMismatchException.class)
    public String handleAccountsCurrenciesMismatchException(AccountsCurrenciesMismatchException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exception", ex.getMessage());
        return "redirect:/atm/transactions/new";
    }

    // TODO: handle both exceptions (CurrencyMismatch, InsufficientFunds)

    // account
    @ExceptionHandler(IOException.class)
    public String handleIOException(IOException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exception",
                 messageServices.getMessage("err.io.exception"));
        return "redirect:/atm/profile";
    }
    // account
    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException ex, RedirectAttributes redirectAttributes) {
        // when u finish the app. u can come back and modify the error messages.
        redirectAttributes.addFlashAttribute("exception", ex.getMessage());
        return "redirect:/atm/profile";
    }
}
