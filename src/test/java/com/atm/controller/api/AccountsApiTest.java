package com.atm.controller.api;

import static org.junit.jupiter.api.Assertions.*;

import com.atm.business.abstracts.AccountServices;
import com.atm.business.concretes.MessageServices;
import com.atm.controller.AccountController;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.dtos.payloads.responses.AccountDto;
import com.atm.model.entities.User;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.*;
@WebMvcTest(controllers = AccountsApi.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@Log4j2
class AccountsApiTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageServices messageServices;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountServices accountServices;




    @Test
    @SneakyThrows
    void accountApiController_FindAll() {
        User user = User.builder().email("esaf")
                .password("apds").firstName("afds")
                .lastName("safd").build();
        user.setId(1L);
        CustomUserDetailsDto usr =
                CustomUserDetailsDto.builder()
                        .user(user)
                        .build();
        Authentication auth =
                new UsernamePasswordAuthenticationToken(usr,
                        null, List.of());
        List<AccountDto> accounts = new ArrayList<>();
        AccountDto account = AccountDto.builder()
                .type("business")
                .currency("$")
                .balance(500)
                .createdDate(LocalDateTime.now())
                .slug("slug")
                .build();
        accounts.add(account);
        Page<AccountDto> accountsPage = new PageImpl<>(accounts);
        BDDMockito
                .given(accountServices.findAll(
                        Mockito.anyLong(),
                        Mockito.anyInt(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString(),
                        Mockito.anyString()
                ))
                .willReturn(accountsPage);

        String jsonResponse = mockMvc
                .perform(
                        get("/api/accounts")
                                .param("page","1")
                                .principal(auth)
                                .param("order","")
                                .param("from","")
                                .param("to","")
                                .param("sortBy","")
                                .param("searchQuery","")
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode root = objectMapper.readTree(jsonResponse);
        // "accounts" is the field name in Map JSON
        // in my controller
        JsonNode content = root.get("accounts");
        JsonNode foundAccount = content.get(0);
        assertEquals(content.size(), 1);
        assertEquals(foundAccount.get("type").asText(),
                "business");
    }


    // JSON Request / Response
    @Test
    @SneakyThrows
    void accountApiController_Delete() {
        BDDMockito
                .given(accountServices.delete(anyString()))
                .willReturn("deleted");
        mockMvc
                .perform(delete("/api/accounts")
                        .param("slug", "slug")
                )
                .andExpect(status().isOk());
    }
}