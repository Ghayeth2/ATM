package com.atm.controller;

import com.atm.business.abstracts.UserService;
import com.atm.business.concretes.MessageServices;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.entities.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import static org.mockito.ArgumentMatchers.any;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired // TestRestTemplate
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private MessageServices messageServices;

    private Authentication auth;


    @BeforeEach
    void setUp() {
        User user = User.builder()
                .password("pass").email("email@gmail.com")
                .firstName("nfsd").lastName("lsdf").build();
        CustomUserDetailsDto userDetails = new CustomUserDetailsDto(user);
        auth = new UsernamePasswordAuthenticationToken(userDetails, null, List.of());
    }

    @Test
    @SneakyThrows
    void userController_UpdateUser_RedirectsToProfileView() {
        Mockito.when(userService.update(any(), anyString()))
                        .thenReturn("updated successfully");
        mockMvc.perform(post("/atm/user/profile")
                .principal(auth)
                .param("email", "email@gmail.com")
                .param("password", "pass")
                .param("firstName", "nfsd")
                .param("lastName", "lsdf")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/atm/profile"));
    }

    @Test
    @SneakyThrows
    void userController_UpdateUser_FieldErrors() {
        mockMvc.perform(post("/atm/user/profile")
                        .principal(auth)
                        .param("email", "om")
                        .param("password", "pass")
                        .param("firstName", "nf234sd")
                        .param("lastName", "lsd22f")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("user",
                        "email",
                        "firstName", "lastName"))
                .andExpect(view()
                        .name("layout/user/profile"));
    }

}