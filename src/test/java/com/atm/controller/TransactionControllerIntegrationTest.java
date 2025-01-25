package com.atm.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * Integration test for Transaction Services.
 */
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Depositing funds into one account.
     */
    @Test @SneakyThrows
    @WithMockUser
    void transactionController_IntegrationTest_Deposit() {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/atm/transactions")
                .param("senderNumber", "")
                .param("receiverNumber",
                        "1000-6097-0682-0033")
                .param("type", "Deposit")
                .param("amount", "1500")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(flash().attribute(
                        "response",
                        "Transaction is saved: Deposit"
                ))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/atm/transactions/new"));
    }

    /**
     * Transferring funds between accounts of different users.
     */
    @Test @SneakyThrows @WithMockUser
    void transactionController_IntegrationTest_Transfer() {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/atm/transactions")
                .param("senderNumber", "1000-6097-0682-0033")
                .param("receiverNumber", "1000-4070-8264-0034")
                .param("type", "Transfer")
                .param("amount", "250")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(flash().attribute("response",
                        "Transaction is saved: Transfer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/atm/transactions/new"));
    }

    @Test @SneakyThrows @WithMockUser
    void transactionController_IntegrationTest_Withdraw() {
        mockMvc.perform(MockMvcRequestBuilders.post("/atm/transactions")
                .param("senderNumber", "")
                .param("receiverNumber", "1000-4070-8264-0034")
                .param("type", "Withdrawal")
                .param("amount", "40")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(flash().attribute("response",
                        "Transaction is saved: Withdrawal"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/atm/transactions/new"));
    }

}