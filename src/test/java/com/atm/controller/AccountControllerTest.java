package com.atm.controller;

import com.atm.business.abstracts.AccountServices;
import com.atm.business.concretes.MessageServices;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.dtos.UserDetailsDto;
import com.atm.model.entities.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.*;
@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountServices accountServices;

    @MockBean
    private MessageServices messageServices;

    @Test
    @SneakyThrows
    void accountController_NewAccount_ReturnsNewAccountView() {
        mockMvc.perform(MockMvcRequestBuilders.get(
                "/atm/accounts/new"
        )).andExpect(status().isOk())
        .andExpect(view()
                .name("layout/accounts/new"));
    }

    @Test
    @SneakyThrows
    void accountController_Index_ReturnsIndexView() {
        mockMvc.perform(get(
                "/atm/accounts"
        )).andExpect(status().isOk())
        .andExpect(view()
        .name("layout/accounts/index"));

    }

    @Test
    @SneakyThrows
    void accountController_SaveAccount_ValidArgs() {
        User user = User.builder()
                .lastName("as").firstName("if")
                .email("test@gmail.com").password("pas")
                .build();
        CustomUserDetailsDto usr = CustomUserDetailsDto
                .builder().user(user).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(usr, null,
                List.of());

        BDDMockito
                .doNothing()
                .when(accountServices)
                .save(anyString(),
                      anyString(),
                        any());
        BDDMockito
                .given(messageServices
                        .getMessage(anyString()))
                .willReturn("Added successfully");
        mockMvc
                .perform(
                        post(
                "/atm/accounts"
                        )
                                .principal(auth)
                        .param("type", "Business Account")
                        .param("currency", "usd")
                ).andExpect(
                        model().attribute("success"
                        ,"Added successfully")
                ).andExpect(
                        status().isOk()
                ).andExpect(
                        view()
                        .name("layout/accounts/new")
                );
    }

    @Test
    @SneakyThrows
    void accountController_SaveAccount_InvalidArgs() {
        User user = User.builder()
                .email("test@gmail.com").password("pas")
                .build();
        CustomUserDetailsDto usr = CustomUserDetailsDto
                .builder().user(user).build();
        Authentication auth =
                new UsernamePasswordAuthenticationToken(usr
                , null, List.of());
        BDDMockito
                .given(messageServices
                .getMessage(anyString()))
                .willReturn("Invalid args!");
        mockMvc.perform(
            post("/atm/accounts")
            .principal(auth)
            .param("type","")
            .param("currency","")
        ).andExpect(model().attribute(
                "error", "Invalid args!"
        )).andExpect(
                view()
                .name("layout/accounts/new")
        );
    }
}