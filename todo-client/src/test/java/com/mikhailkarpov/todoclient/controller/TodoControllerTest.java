package com.mikhailkarpov.todoclient.controller;

import com.mikhailkarpov.todoclient.exception.TodoServiceException;
import com.mikhailkarpov.todoclient.model.Todo;
import com.mikhailkarpov.todoclient.service.TodoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TodoController.class)
class TodoControllerTest {

    @MockBean
    ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    private static OAuth2AuthenticationToken buildPrincipal() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "test-user");

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");

        OAuth2User oAuth2User = new DefaultOAuth2User(authorities, attributes, "sub");

        return new OAuth2AuthenticationToken(oAuth2User, authorities, "whatever");
    }

    @Test
    void givenOAuth2Token_whenGetTodo_thenOk() throws Exception {
        //given
        OAuth2AuthenticationToken token = buildPrincipal();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, new SecurityContextImpl(token));

        when(todoService.findByUserName(any(String.class))).thenReturn(Arrays.asList(
                new Todo(1L, "test-user", "todo 1", true),
                new Todo(2L, "test-user", "todo 2", false)
        ));

        mockMvc.perform(get("/todo")
                .session(session)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasNoErrors())
                .andExpect(model().attribute("todoList", hasSize(2)))
                .andExpect(model().attribute("todoForm", notNullValue()))
                .andExpect(view().name("todo"));

        verify(todoService).findByUserName("test-user");
        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenOAuth2Token_whenPostTodo_thenOk() throws Exception {
        //given
        OAuth2AuthenticationToken token = buildPrincipal();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, new SecurityContextImpl(token));

        mockMvc.perform(post("/todo")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("description", "todo 1")
                .session(session)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors())
                .andExpect(redirectedUrl("/todo"));

        verify(todoService).save(any(Todo.class));
        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenOAuth2Token_whenPostTodoUpdate_thenOk() throws Exception {
        //given
        OAuth2AuthenticationToken token = buildPrincipal();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, new SecurityContextImpl(token));

        mockMvc.perform(post("/todo/update")
                .contentType(APPLICATION_FORM_URLENCODED)
                .param("id", "12")
                .param("description", "todo 12")
                .param("ownerId", "test-user")
                .param("completed", "true")
                .session(session)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors())
                .andExpect(redirectedUrl("/todo"));

        verify(todoService).update(any(), any());
        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenOAuth2Token_whenPostTodoDelete_thenOk() throws Exception {
        //given
        OAuth2AuthenticationToken token = buildPrincipal();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, new SecurityContextImpl(token));

        mockMvc.perform(post("/todo/delete?id=12")
                .session(session)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().hasNoErrors())
                .andExpect(redirectedUrl("/todo"));

        verify(todoService).delete(12L);
        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenOAuth2Token_whenTodoServiceThrows_thenErrorPage() throws Exception {
        //given
        OAuth2AuthenticationToken token = buildPrincipal();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, new SecurityContextImpl(token));

        doThrow(new TodoServiceException(HttpStatus.FORBIDDEN)).when(todoService).findByUserName(any());
        doThrow(new TodoServiceException(HttpStatus.BAD_REQUEST)).when(todoService).save(any());
        doThrow(new TodoServiceException(HttpStatus.FORBIDDEN)).when(todoService).update(any(), any());
        doThrow(new TodoServiceException(HttpStatus.NOT_FOUND)).when(todoService).delete(any());

        mockMvc.perform(get("/todo")
                .session(session)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));

        mockMvc.perform(post("/todo")
                .session(session)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));

        mockMvc.perform(post("/todo/update")
                .session(session)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));

        mockMvc.perform(post("/todo/delete")
                .session(session)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("error"));
    }
}