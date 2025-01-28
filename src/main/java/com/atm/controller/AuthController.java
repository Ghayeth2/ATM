package com.atm.controller;

import com.atm.business.abstracts.ConfirmationTokenServices;
import com.atm.business.abstracts.TempUserServices;
import com.atm.business.abstracts.UserService;
import com.atm.core.exceptions.AccountInactiveException;
import com.atm.core.exceptions.EmailExistsException;
import com.atm.model.dtos.payloads.requests.ResetPasswordReq;
import com.atm.model.dtos.UserDto;
import com.atm.model.entities.ConfirmationToken;
import com.atm.model.entities.User;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
// if all were /auth it would work in security chain by only setting /atm/auth**
@RequestMapping("/atm/user")
@Log4j2
@AllArgsConstructor
public class AuthController {

    private UserService userService;
    private TempUserServices tempUserServices;
    private ConfirmationTokenServices confirmationTokenServices;

    @PostMapping
    public String save(@Valid @ModelAttribute("user") UserDto userDto,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes) throws EmailExistsException {

        if(!userDto.getPassword().equals(userDto.getPassword2()))
            return "redirect:/atm/registration?notMatched";
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", userDto);
//            log.error("An error occurred!");
            return "layout/auth/signup";
        }
        String successMessage = tempUserServices.save(userDto);
        redirectAttributes.addFlashAttribute("success",
                successMessage);
        return "redirect:/atm/registration";
    }

    // first page of forgetting password, entering email
    @PostMapping("/password/reset")
    public String resetPassword(@RequestParam("email")
                                    String email, RedirectAttributes ra) {
        userService.handleResetPasswortMailSending(email);
        ra.addFlashAttribute("sent",
                "Check your email for reset password");
        return "redirect:/atm/password/forgot";
    }

    // the action of resetting password, coming from its html page
    @PostMapping("/reset/password")
    public String passwordReset(@Valid@ModelAttribute("password_obj")
                                    ResetPasswordReq resetPasswordReq,
                                RedirectAttributes ra
                , @RequestParam("token") String token) {
        if (!resetPasswordReq.getPassword().equals(resetPasswordReq.getPassword2())) {
            return "redirect:/atm/reset/password?notMatched";
        }
        User user = userService.findUserByToken(token);
        String res = userService.resetPassword(resetPasswordReq.getPassword(),
                user.getSlug());
        ra.addFlashAttribute("success",
                res);
        return "redirect:/atm/reset/password";
    }

    // validating token, redirecting to reset password page / error page
    // All tokens should be stored in Redis client for temp usages.
    @GetMapping("/reset")
    public String checkToken(@RequestParam("token") String token,
                             Model model, RedirectAttributes ra) {
        boolean isValid = confirmationTokenServices.isTokenValid(token);
        if (isValid) {
            // To bind user to the new token
            User user = userService.findUserByToken(token);
            ConfirmationToken tkn = confirmationTokenServices
                    .newConfirmationToken(user.getEmail());
            confirmationTokenServices.saveConfirmationToken(tkn);
            model.addAttribute("password_obj",
                    new ResetPasswordReq());
            ra.addAttribute("token", tkn.getToken());
            return "redirect:/atm/reset/password";
        }
        model.addAttribute("error",
                "Cannot reset password, try again.");
        return "layout/auth/error_reset_page";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam("token") String token,
                         RedirectAttributes ra)
            throws AccountInactiveException {
       String res = confirmationTokenServices.confirmToken(token);
       userService.save(token);
       ra.addFlashAttribute("illegal", false);
       ra.addFlashAttribute("message", res);
        return "redirect:/atm/email_confirmed";
    }
}
