package com.atm.core.controllerAdviser;

import com.atm.core.exceptions.AccountInactiveException;
import com.atm.core.exceptions.EmailExistsException;
import com.atm.core.exceptions.PasswordMisMatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

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

//    @ExceptionHandler(IllegalStateException.class)
//    public String handleIllegalStateException(IllegalStateException ex, RedirectAttributes redirectAttributes
//                    ) {
//
//        return "redirect:/atm/email_confirmed";
//    }

    // Perhaps, u can modify this one to make as above ones redirect:/atm/login with flush attribute
    @ExceptionHandler(AccountInactiveException.class)

    public String handleAccountInactiveException(AccountInactiveException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("illegal", true);
        redirectAttributes.addFlashAttribute("message", ex.getMessage());
        return "redirect:/atm/email_confirmed";
    }

    // account
    @ExceptionHandler(IOException.class)
    public String handleIOException(IOException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exception",
                "Couldn't update lead/tail account numbers, " +ex.getMessage());
        return "redirect:/atm/profile";
    }
    // account
    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException ex, RedirectAttributes redirectAttributes) {
        // when u finish the app. u can come back and modify the error messages.
        redirectAttributes.addFlashAttribute("exception", ex.getMessage());
        return "redirect:/atm/profile";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("exception", ex.getMessage());
        return "redirect:/atm/login";
    }
}
