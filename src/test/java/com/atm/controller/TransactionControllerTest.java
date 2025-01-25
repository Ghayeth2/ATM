package com.atm.controller;


import com.atm.business.abstracts.TransactionsServices;
import com.atm.business.concretes.MessageServices;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
/**
 * Unit Testing TransactionController
 */
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = TransactionController.class)
@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageServices messageServices;

    @MockBean
    private TransactionsServices transactionServices;

    /**
     * testing newTransaction method when data is valid
     */
    @Test @SneakyThrows
//    @WithMockUser
    void transactionController_NewTransaction() {
        // Mocking service's response
        Mockito.when(transactionServices.newTransaction(
                Mockito.anyString(),
                Mockito.anyString(),
                // Not any Object rather than Object[]
                Mockito.any(String[].class)
        )).thenReturn("success");
        // Sending request with required data
        mockMvc.perform(MockMvcRequestBuilders
                .post("/atm/transactions")
                .param("senderNumber", "")
                .param("receiverNumber",
                        "1549-5659-9586-6585")
                .param("type", "Withdrawal")
                .param("amount", "600.00")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        ).andExpect(flash().attribute("response", "success"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/atm/transactions/new"));
    }


}