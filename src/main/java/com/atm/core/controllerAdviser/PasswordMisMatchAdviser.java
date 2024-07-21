package com.atm.core.controllerAdviser;

import com.atm.core.exception.EmailExistsException;
import com.atm.core.exception.PasswordMisMatchException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class PasswordMisMatchAdviser {
    @ExceptionHandler(PasswordMisMatchException.class)
    public String handleEmailExistsException(PasswordMisMatchException ex, RedirectAttributes redirectAttributes) {
        // Password mismatching attribute to get the message in frontend
        redirectAttributes.addFlashAttribute("misMatch", ex.getMessage());
        return "redirect:/atm/profile";
    }
}
