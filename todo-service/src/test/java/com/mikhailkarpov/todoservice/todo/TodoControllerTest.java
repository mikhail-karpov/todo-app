package com.mikhailkarpov.todoservice.todo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Test
    void givenNoToken_thenUnauthorized() throws Exception {

        mockMvc.perform(get("/todo"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/todo/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/todo/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/todo/1"))
                .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenSubject_whenGet_thenOk() throws Exception {

        when(todoService.findAllByOwnerId("test_user")).thenReturn(Arrays.asList(
                new TodoDto(1L, "test_user", "todo 1", true),
                new TodoDto(2L, "test_user", "todo 2", false)
        ));

        mockMvc.perform(get("/todo")
                .with(jwt().jwt(jwt -> jwt.subject("test_user")))
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].owner_id").value("test_user"))
                .andExpect(jsonPath("$[0].description").value("todo 1"))
                .andExpect(jsonPath("$[0].completed").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].owner_id").value("test_user"))
                .andExpect(jsonPath("$[1].description").value("todo 2"))
                .andExpect(jsonPath("$[1].completed").value(false));

        when(todoService.findAllByOwnerId("test_user")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/todo")
                .with(jwt().jwt(jwt -> jwt.subject("test_user"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(todoService, times(2)).findAllByOwnerId("test_user");
        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenSubject_whenGetById_thenOk() throws Exception {

        TodoDto todo = new TodoDto(14L, "test_user", "todo 14", false);
        JwtRequestPostProcessor test_user_jwt = jwt().jwt(jwt -> jwt.subject("test_user"));

        when(todoService.findById(14L)).thenReturn(todo);
        mockMvc.perform(get("/todo/14")
                .with(test_user_jwt)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(14))
                .andExpect(jsonPath("$.owner_id").value("test_user"))
                .andExpect(jsonPath("$.description").value("todo 14"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(todoService).findById(14L);
        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenNotOwner_whenGetById_thenForbidden() throws Exception {

        TodoDto todo = new TodoDto(14L, "test_user", "todo 14", false);
        JwtRequestPostProcessor test_user_jwt = jwt().jwt(jwt -> jwt.subject("not_owner"));

        when(todoService.findById(14L)).thenReturn(todo);
        mockMvc.perform(get("/todo/14")
                .with(test_user_jwt))
                .andExpect(status().isForbidden());

        verify(todoService).findById(14L);
        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenValidTodo_whenPost_thenCreated() throws Exception {

        TodoDto request = new TodoDto();
        request.setOwnerId("test_user");
        request.setDescription("todo 1");
        request.setCompleted(true);

        TodoDto response = new TodoDto();
        response.setId(12L);
        response.setOwnerId("test_user");
        response.setDescription("todo 1");
        response.setCompleted(true);

        when(todoService.save(request)).thenReturn(response);

        mockMvc.perform(post("/todo")
                .with(jwt().jwt(jwt -> jwt.subject("test_user")))
                .contentType(APPLICATION_JSON)
                .content("{\"description\": \"todo 1\", \"completed\": true}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/todo/12"))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.owner_id").value("test_user"))
                .andExpect(jsonPath("$.description").value("todo 1"))
                .andExpect(jsonPath("$.completed").value(true));

        verify(todoService).save(request);
        verifyNoMoreInteractions(todoService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "{\"completed\": true}", "{\"description\": \"todo\"}"})
    void givenInvalidTodo_whenPost_thenBadRequest(String body) throws Exception {

        JwtRequestPostProcessor test_user = jwt().jwt(jwt -> jwt.subject("test_user"));

        mockMvc.perform(post("/todo").with(test_user)
                .contentType(APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenValidTodo_whenUpdate_thenOk() throws Exception {

        TodoDto todo = new TodoDto(12L, "test_user", "todo 1", false);
        when(todoService.update(12L, todo)).thenReturn(todo);

        mockMvc.perform(put("/todo/12")
                .accept(APPLICATION_JSON)
                .with(jwt().jwt(jwt -> jwt.subject("test_user")))
                .contentType(APPLICATION_JSON)
                .content("{\"id\": 12, \"owner_id\": \"test_user\", \"description\": \"todo 1\", \"completed\": false}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.owner_id").value("test_user"))
                .andExpect(jsonPath("$.description").value("todo 1"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(todoService).update(12L, todo);
        verifyNoMoreInteractions(todoService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "{\"completed\": true}", "{\"description\": \"todo\"}"})
    void givenInvalidTodo_whenUpdate_thenBadRequest(String body) throws Exception {

        mockMvc.perform(put("/todo/12")
                .with(jwt().jwt(jwt -> jwt.subject("test_user")))
                .contentType(APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenNotOwner_whenUpdate_thenForbidden() throws Exception {

        TodoDto todo = new TodoDto(12L, "test_user", "todo 1", false);
        when(todoService.update(12L, todo)).thenReturn(todo);

        mockMvc.perform(put("/todo/12")
                .accept(APPLICATION_JSON)
                .with(jwt().jwt(jwt -> jwt.subject("not_owner")))
                .contentType(APPLICATION_JSON)
                .content("{\"id\": 12, \"owner_id\": \"test_user\", \"description\": \"todo 1\", \"completed\": false}"))
                .andExpect(status().isForbidden());

        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenTodoFound_whenDelete_thenNoContent() throws Exception {

        TodoDto todo = new TodoDto(1L, "test_user", "todo 1", false);
        when(todoService.findById(1L)).thenReturn(todo);

        mockMvc.perform(delete("/todo/1")
                .with(jwt().jwt(jwt -> jwt.subject("test_user"))))
                .andExpect(status().isNoContent());

        verify(todoService).findById(1L);
        verify(todoService).delete(1L, "test_user");
        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenNotOwner_whenDelete_thenForbidden() throws Exception {

        TodoDto todo = new TodoDto(1L, "test_user", "todo 1", false);
        when(todoService.findById(1L)).thenReturn(todo);

        mockMvc.perform(delete("/todo/1")
                .with(jwt().jwt(jwt -> jwt.subject("not_owner"))))
                .andExpect(status().isForbidden());

        verify(todoService).findById(1L);
        verifyNoMoreInteractions(todoService);
    }
}