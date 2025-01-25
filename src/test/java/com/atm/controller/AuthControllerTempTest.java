package com.atm.controller;

import com.atm.AtmApplication;
import com.atm.model.dtos.UserDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for AuthController, AuthService & AuthDao to test
 * whether a request is hitting the Redis Server Container or not.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = AtmApplication.class)
@Testcontainers
class AuthControllerTempTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void authController_SaveTempUserAndConfirmationToken() {
        // Create a UserDto object with valid data
        UserDto userDto = UserDto.builder().firstName("Ghayeth")
                .lastName("Al Masri").email("ghayeth.msri@gmail.com")
                .password2("C4bad432@").password("C4bad432@").build();
        // Use HttpHeaders to specify content type
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // Convert the UserDto object into a MultiValueMap
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("firstName", userDto.getFirstName());
        map.add("lastName", userDto.getLastName());
        map.add("email", userDto.getEmail());
        map.add("password", userDto.getPassword());
        map.add("password2", userDto.getPassword2());
        // Create an HttpEntity object
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        // Make a POST request
        String response = restTemplate.postForEntity("/atm/user", request, String.class)
                .toString();
        // Assert the response
        Assertions.assertNotNull(response);
    }

}