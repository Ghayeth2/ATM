package com.atm.controller.api;

import com.atm.business.abstracts.ConfigService;
import com.atm.business.abstracts.SettingsServices;
import com.atm.business.concretes.MessageServices;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;
@AutoConfigureMockMvc
@SpringBootTest
class SettingsApiTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Integration Testing pageLimits
     */
    @Test @SneakyThrows @WithMockUser
    void settingsApi_PageLimits() {
        // Sending post request to the endpoint
        String response = mockMvc.perform(post("/atm/api/settings/page")
                .param("accountsLimit", "3")
                .param("transactionsLimit", "3")
                .param("adminTrsLimit", "3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        // Asserting returned result from the sent request
        Assertions.assertThat(response).isEqualTo(
                "Changes are saved successfully"
        );
    }

    /**
     * Integration testing feesSettings method
     */
    @Test @SneakyThrows @WithMockUser
    void settingsApi_FeesSettings() {
        // Building and sending the request to target endpoint
        String response = mockMvc.perform(post(
                "/atm/api/settings/fees"
        ).param("personalFee", "0.03")
        .param("businessFee", "0.03")
        .param("savingsFee", "0.03")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        // Asserting returned response
        Assertions.assertThat(response).isEqualTo(
                "Changes are saved successfully"
        );
    }

    /**
     * Integration testing numbersSettings
     */
    @Test @SneakyThrows @WithMockUser
    void settingsApi_NumbersSettings() {
        // Building and sending the request
        String response = mockMvc.perform(
                post("/atm/api/settings/numbers")
                        .param("leadingNumber", "0000")
                        .param("tailingNumber","1001")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString();
        // Asserting returned response
        Assertions.assertThat(response).isEqualTo(
                "Changes are saved successfully"
        );
    }

}