package com.atm.controller.api;

import com.atm.business.abstracts.TransactionsServices;
import com.atm.business.concretes.MessageServices;
import com.atm.model.dtos.payloads.records.requests.TransactionsFiltersRequest;
import com.atm.model.dtos.payloads.records.responses.TransactionDto;
import com.atm.model.dtos.payloads.records.responses.UserAccountTransaction;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TransactionsApi.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class TransactionsApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageServices messageServices;

    @MockBean @Qualifier("transactionsManager")
    private TransactionsServices transactionsServices;


    @Test @WithMockUser
    @SneakyThrows
    void transactionsApi_FindAllFiltered() {
        // Mocked data
        Page<UserAccountTransaction> data =
                new PageImpl<>(
                        List.of(
                                UserAccountTransaction.builder()
                                        .fullName("ghayeth al masri")
                                        .email("test@gmail.com")
                                        .accountType("savings")
                                        .transactionType("transfer")
                                        .build()
                        )
                );
        // Mocked response
        Mockito.when(transactionsServices.findAllFiltered(any(
                TransactionsFiltersRequest.class
        ))).thenReturn(data);
        // Send request
        String response = mockMvc.perform(
                get("/atm/api/transactions/all")
                        .param("searchQuery","")
                        .param("sortBy", "")
                        .param("sortOrder", "")
                        .param("fromDate", "")
                        .param("toDate", "")
                        .param("fromAmount", "0.0")
                        .param("toAmount", "0.0")
                        .param("page", "1")
        ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        // Reading data from JSON
        String fullName = JsonPath.read(response,
                "$.transactions[0].fullName");
        // Asserting result
        Assertions.assertEquals("ghayeth al masri", fullName);
    }


    @Test @SneakyThrows
    @WithMockUser
    void transactionsApi_FindAllByAccount() {
        // Mocked data
        Page<TransactionDto> data = new PageImpl<>(
                List.of(
                        TransactionDto.builder()
                        .type("type").amount(500).createdDate(
                                LocalDateTime.now()
                                ).balanceAfter(500).build()
                )
        );
        // Mocked response
        Mockito.when(transactionsServices.findAllByAccount(
                anyString(), anyString(), anyString(), anyInt(),
                anyString(), anyString()
        )).thenReturn(data);
        // Sending request
        String response = mockMvc.perform(
                get("/atm/api/transactions/user")
                        .param("slug", "slug")
                        .param("page", "1")
                        .param("from", "")
                        .param("to", "")
                        .param("sortOrder", "")
                        .param("sortBy", "")
        ).andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        // Reading JSON
        int totalElements = JsonPath.read(response,
                "$.totalElements");
        String type = JsonPath.read(response,
                "$.transactions[0].type");
        // Asserting results
        Assertions.assertEquals("type", type);
        Assertions.assertEquals(totalElements, 1);
    }

}