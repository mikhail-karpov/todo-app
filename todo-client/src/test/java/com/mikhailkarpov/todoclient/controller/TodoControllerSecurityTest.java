package com.mikhailkarpov.todoclient.controller;

import com.mikhailkarpov.todoclient.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TodoController.class)
public class TodoControllerSecurityTest {

    @MockBean
    ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Test
    void givenNoCSRFToken_whenPost_thenForbidden() throws Exception {

        mockMvc.perform(post("/todo"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/todo/update"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/todo/delete"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenNoAuthorizationPrincipal_whenHitEndpoints_thenRedirectToLogin() throws Exception {

        mockMvc.perform(get("/todo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        mockMvc.perform(post("/todo")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        mockMvc.perform(post("/todo/update")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        mockMvc.perform(post("/todo/delete")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

}
