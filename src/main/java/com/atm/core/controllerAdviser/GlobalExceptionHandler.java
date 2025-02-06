package com.atm.core.controllerAdviser;

import com.atm.business.concretes.MessageServices;
import com.atm.core.exceptions.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
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
     * Handler for InsufficientFundsExceptionWithdraw
     *
     * @param ex
     * @param redirectAttributes
     * @return
     */
    @ExceptionHandler(InsufficientFundsExceptionWithdraw.class)
    public String handleInsufficientFundsException(InsufficientFundsExceptionWithdraw ex,
                                                   RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exception", ex.getMessage());
        log.error("GlobalExceptionHandler -> handleInsufficientFundsExceptionWithdraw");
        log.error("GlobalExceptionHandler -> redirecting with error message...");
        return "redirect:/atm/withdraw";
    }

    // NotFoundException handle it for each place it might occur and return it to right place

    /**
     * Handler for InsufficientFundsExceptionDeposit
     *
     * @param ex
     * @param redirectAttributes
     * @return
     */
    @ExceptionHandler(InsufficientFundsExceptionDeposit.class)
    public String handleInsufficientFundsException(InsufficientFundsExceptionDeposit ex,
                                                   RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exception", ex.getMessage());
        log.error("GlobalExceptionHandler -> handleInsufficientFundsExceptionDeposit");
        log.error("GlobalExceptionHandler -> redirecting with error message...");
        return "redirect:/atm/deposit";
    }

    /**
     * Handler for InsufficientFundsExceptionTransfer
     *
     * @param ex
     * @param redirectAttributes
     * @return
     */
    @ExceptionHandler(InsufficientFundsExceptionTransfer.class)
    public String handleInsufficientFundsException(InsufficientFundsExceptionTransfer ex,
                                                   RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exception", ex.getMessage());
        return "redirect:/atm/transfer";
    }

    /**
     * Handler for AccountsCurrenciesMismatchException during transfer transaction
     * if the currencies of the two accounts differ.
     *
     * @param ex
     * @param redirectAttributes
     * @return
     */
    @ExceptionHandler(AccountsCurrenciesMismatchException.class)
    public String handleAccountsCurrenciesMismatchException(AccountsCurrenciesMismatchException ex,
                                                            RedirectAttributes redirectAttributes) {
        log.error("GlobalExceptionHandler -> handleAccountsCurrenciesMismatchException");
        redirectAttributes.addFlashAttribute("exception", ex.getMessage());
        log.error("GlobalExceptionHandler -> redirecting with exception message...");
        return "redirect:/atm/transfer";
    }

    @ExceptionHandler(RuntimeException.class)
    public Map<String, String> handleRuntimeException(RuntimeException ex) {
        log.error("GlobalExceptionHandler -> handleRuntimeException");
        log.error("GlobalExceptionHandler -> redirecting with exception message...");
        Map<String, String> map = new HashMap<>();
        map.put("error", ex.getMessage());
        return map;
    }

    // TODO: handle both exceptions (CurrencyMismatch, InsufficientFunds)

    // account
    @ExceptionHandler(IOException.class)
    public String handleIOException(IOException ex, RedirectAttributes redirectAttributes) {
        // Adding logger
        System.out.println("Is there exception being caught??");
        log.error("GlobalExceptionHandler -> handleIOException: "+ex.getMessage());
        redirectAttributes.addFlashAttribute("exception",
                messageServices.getMessage("err.io.exception"));
        return "redirect:/atm/profile";
    }

    @ExceptionHandler(NoConfigKeyFoundException.class)
    public Map<String, String> handleNoConfigKeyFoundException(NoConfigKeyFoundException ex) {
        log.error("GlobalExceptionHandler -> handleNoConfigKeyFoundException");
        Map<String, String> map = new HashMap<>();
        map.put("error", ex.getMessage());
        return map;
    }

    // account
    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException ex, RedirectAttributes redirectAttributes) {
        // when u finish the app. u can come back and modify the error messages.
        redirectAttributes.addFlashAttribute("exception", ex.getMessage());
        return "redirect:/atm/profile";
    }
}
