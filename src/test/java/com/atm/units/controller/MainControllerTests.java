package com.atm.units.controller;

import com.atm.business.concretes.MessageServices;
import com.atm.controller.MainController;
import com.atm.core.config.TestAuditingConfig;
import com.atm.core.exceptions.AccountInactiveException;
import com.atm.core.utils.converter.DtoEntityConverter;
import com.atm.model.dtos.CustomUserDetailsDto;
import com.atm.model.dtos.UserDetailsDto;
import com.atm.model.entities.User;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.ui.Model;


import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = MainController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class MainControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Model model;

    @InjectMocks
    private MainController controller;

    @MockBean
    private DtoEntityConverter converter;
    // Mocking the MessageServices so Spring won't
    // try to create a bean from it.
    @MockBean
    private MessageServices messageServices;

    @MockBean
    private Authentication authentication;

    @SneakyThrows
    // returning home page
    @Test
    void mainControllerTest_ReturnsHomePage() {
        mockMvc.perform(get("/atm")
                ).andExpect(status().isOk())
                .andExpect(view()
                        .name("layout/home"));
    }

    @SneakyThrows
    @Test
    void mainControllerTest_ReturnLoginPage() {
        mockMvc.perform(
                get("/atm/login")
        ).andExpect(status().isOk()).andExpect(view()
                .name("layout/auth/login"));
    }

    @SneakyThrows
    @Test
    void mainControllerTest_ReturnsEmailConfirmedTemplate() {
        mockMvc.perform(
               get(
                       "/atm/email_confirmed"
               )
        ).andExpect(status().isOk())
                .andExpect(model().attribute("illegal",
                        false))
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name(
                        "layout/auth/email_confirmed"
                ));
    }

    @Test
    @SneakyThrows
    void mainController_SignUp_ReturnsSignUpViewWithUserObject() {
        mockMvc.perform(get("/atm/registration"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("layout/auth/signup"));
    }

    @Test
    @SneakyThrows
    void mainController_Profile_ReturnsProfileViewWithUserObject() {
       User usr = User.builder().firstName("first").lastName("last")
                       .email("email").password("password").build();
       CustomUserDetailsDto principal = new CustomUserDetailsDto(usr);
       principal.setUser(usr);
       Authentication auth = new UsernamePasswordAuthenticationToken(principal
       ,null, List.of());
        UserDetailsDto usrDetails = UserDetailsDto.builder().email(usr.getEmail())
                        .lastName(usr.getLastName()).firstName(usr.getFirstName())
                        .password(usr.getPassword()).build();
        given(converter.entityToDto(any(), any())).willReturn(usrDetails);
       mockMvc.perform(get("/atm/profile").principal(auth))
               .andExpect(status().isOk())
               .andExpect(model().attribute("user", usrDetails))
               .andExpect(view().name("layout/user/profile"));

    }

    @SneakyThrows
    @Test
    void mainController_ForgotPassword_ReturnsForgotPasswordView() {
       mockMvc.perform(
               get("/atm/password/forgot")
       ).andExpect(view().name(
               "layout/auth/email_resetpass"
       ));
    }

    @SneakyThrows
    @Test
    void mainController_ResetPassword_ReturnsResetPasswordView() {
        mockMvc.perform(
                get("/atm/reset/password")
        ).andExpect(view().name(
                "layout/auth/resetPassword"
        ));
    }

    @SneakyThrows
    @Test
    void mainControllerTest_ThrowsEmailAlreadyConfirmedException() {
        mockMvc.perform(
                get("/atm/email_confirmed")
                .flashAttr("illegal", true)
                .flashAttr("message", "Email already confirmed")
                )
                .andExpect(model()
                        .attribute("illegal", true))
                .andExpect(model()
                        .attribute("message",
                                "Email already confirmed"));
    }
}
