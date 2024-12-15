package com.atm.controller;

import com.atm.business.abstracts.ConfirmationTokenServices;
import com.atm.business.abstracts.UserAccountServices;
import com.atm.business.abstracts.UserService;
import com.atm.business.concretes.MessageServices;
import com.atm.model.dtos.payloads.requests.ResetPasswordReq;
import com.atm.model.entities.ConfirmationToken;
import com.atm.model.entities.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.BDDMockito.given;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.doNothing;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private MessageServices messageServices;

    @MockBean
    private UserAccountServices userAccountServices;

    @MockBean
    private ConfirmationTokenServices cTokenServices;

    private String prefix = null;
    @BeforeEach
    void setUp() {
        prefix = "/atm/user";
    }

    @Test
    @SneakyThrows
    void authController_SaveUser_ValidUserIsGiven() {
        String successMessage = "Success";
        given(userService.save(any())).willReturn(successMessage);
        mockMvc.perform(post(prefix)
                .param("firstName", "first")
                .param("lastName", "last")
                .param("email", "test@gmail.com")
                .param("password", "Password2@")
                .param("password2", "Password2@")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/atm/registration"))
                .andExpect(flash().attribute("success", successMessage));
    }

    @Test
    @SneakyThrows
    void authController_SaveUser_ValidationError() {
        mockMvc.perform(post(prefix)
                .param("firstName", "")
                .param("lastName", "")
                .param("email", "test")
                .param("password", "pass")
                .param("password2", "pass")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("user",
                        "firstName", "lastName", "email", "password"));
    }

    @Test
    @SneakyThrows
    void authController_SaveUser_PasswordMismatch() {
        mockMvc.perform(post(prefix)
                .param("firstName", "safd")
                .param("lastName", "sdf")
                .param("email", "test@gmail.com")
                .param("password", "sfd")
                .param("password2", "pass")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(redirectedUrl("/atm/registration?notMatched"));
    }

    @Test
    @SneakyThrows
    void authController_ResetPassword_SendingEmail() {
        doNothing().when(userService).resetPasswordSender(anyString());
        mockMvc.perform(post(prefix+"/password/reset")
                .param("email", "test@gmail.com"))
                .andExpect(flash().attributeExists("sent"))
                .andExpect(redirectedUrl("/atm/password/forgot"));
    }

    @Test
    @SneakyThrows
    void authController_ResetPassword_ResettingThePassword() {
        String message = "Password reset successful";
        User user = User.builder().email("test@gmail.com")
                .password("pas").firstName("sdfa")
                .lastName("fsdfo").build();
        when(userService.findUserByToken(anyString())).thenReturn(user);
        when(userService.resetPassword(anyString(), anyString()))
                .thenReturn(message);
        mockMvc.perform(post(prefix+"/reset/password")
                .param("password", "Password2@")
                .param("password2", "Password2@")
                .param("token", "token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
//                .andExpect(flash().attribute("success", message))
                .andExpect(redirectedUrl("/atm/reset/password"));
    }

    @Test
    @SneakyThrows
    void authController_CheckToken_TokenIsValid() {
        ConfirmationToken token = ConfirmationToken.builder().token("token").build();
        User user = User.builder().email("email@gmail.com")
                .firstName("jasofd").password("Passesfe@").build();
        when(cTokenServices.isTokenValid(anyString())).thenReturn(true);
        when(userService.findUserByToken(anyString())).thenReturn(user);
        when(cTokenServices.newConfirmationToken(any())).thenReturn(token);
        doNothing().when(cTokenServices).saveConfirmationToken(token);
        mockMvc.perform(get(prefix+"/reset")
        .param("token", "token"))
//                .andExpect(model().attribute("password_obj",
//                        new ResetPasswordReq()))
//                .andExpect(flash().attributeExists("token"))
                .andExpect(redirectedUrl("/atm/reset/password?token="
                + token.getToken()));
    }

    @Test
    @SneakyThrows
    void authController_CheckToken_InvalidToken() {
        when(cTokenServices.isTokenValid(anyString())).thenReturn(false);
        mockMvc.perform(get(prefix+"/reset")
                .param("token", "token"))
                .andExpect(model().attribute("error",
                        "Cannot reset password, try again."))
                .andExpect(view()
                        .name("layout/auth/error_reset_page"));
    }

    @Test
    @SneakyThrows
    void authController_Verify_VerifyingEmail() {
        mockMvc.perform(get(prefix+"/verify")
                .param("token", "token"))
                .andExpect(flash().attributeCount(2))
                .andExpect(redirectedUrl("/atm/email_confirmed"));
        when(cTokenServices.confirmToken(anyString())).thenReturn("Confirmed");
        doNothing().when(userAccountServices).activateAccount(anyString());
    }
}