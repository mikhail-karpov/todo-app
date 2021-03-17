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
import java.util.List;

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

    private final JwtRequestPostProcessor ownerJwt = jwt().jwt(jwt -> jwt.subject("test_user"));

    private final JwtRequestPostProcessor notOwnerJwt = jwt().jwt(jwt -> jwt.subject("not-owner"));

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
    void givenTodoList_whenGet_thenOk() throws Exception {

        List<TodoDto> expectedList = Arrays.asList(
                new TodoDto(1L, "test_user", "todo 1", true),
                new TodoDto(2L, "test_user", "todo 2", false)
        );

        when(todoService.findAllByOwnerId("test_user")).thenReturn(expectedList);

        mockMvc.perform(get("/todo")
                .with(ownerJwt)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].owner-id").value("test_user"))
                .andExpect(jsonPath("$[0].description").value("todo 1"))
                .andExpect(jsonPath("$[0].completed").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].owner-id").value("test_user"))
                .andExpect(jsonPath("$[1].description").value("todo 2"))
                .andExpect(jsonPath("$[1].completed").value(false));

        verify(todoService).findAllByOwnerId("test_user");
        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenEmptyList_whenGet_thenOk() throws Exception {
        when(todoService.findAllByOwnerId("test_user")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/todo")
                .with(ownerJwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(todoService).findAllByOwnerId("test_user");
        verifyNoMoreInteractions(todoService);
    }


    @Test
    void givenTodo_whenGetById_thenOk() throws Exception {

        TodoDto todo = new TodoDto(14L, "test_user", "todo 14", false);

        when(todoService.findById(14L)).thenReturn(todo);

        mockMvc.perform(get("/todo/14")
                .with(ownerJwt)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(14))
                .andExpect(jsonPath("$.owner-id").value("test_user"))
                .andExpect(jsonPath("$.description").value("todo 14"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(todoService).findById(14L);
        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenAnothersTodo_whenGetById_thenForbidden() throws Exception {

        TodoDto todo = new TodoDto(14L, "test_user", "todo 14", false);

        when(todoService.findById(14L)).thenReturn(todo);
        mockMvc.perform(get("/todo/14")
                .with(notOwnerJwt))
                .andExpect(status().isForbidden());

        verify(todoService).findById(14L);
        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenNotFoundTodo_whenGetById_thenNotFound() throws Exception {

        when(todoService.findById(14L)).thenThrow(TodoNotFoundException.class);

        mockMvc.perform(get("/todo/14")
                .with(ownerJwt))
                .andExpect(status().isNotFound());

        verify(todoService).findById(14L);
        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenValidTodo_whenPost_thenCreated() throws Exception {

        TodoDto saveRequest = new TodoDto();
        saveRequest.setOwnerId("test_user");
        saveRequest.setDescription("todo 1");
        saveRequest.setCompleted(true);

        TodoDto saved = new TodoDto();
        saved.setId(12L);
        saved.setOwnerId("test_user");
        saved.setDescription("todo 1");
        saved.setCompleted(true);

        when(todoService.save(saveRequest)).thenReturn(saved);

        mockMvc.perform(post("/todo")
                .with(jwt().jwt(jwt -> jwt.subject("test_user")))
                .contentType(APPLICATION_JSON)
                .content("{\"description\": \"todo 1\", \"completed\": true}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/todo/12"))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.owner-id").value("test_user"))
                .andExpect(jsonPath("$.description").value("todo 1"))
                .andExpect(jsonPath("$.completed").value(true));

        verify(todoService).save(saveRequest);
        verifyNoMoreInteractions(todoService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "{\"completed\": true}", "{\"description\": \"todo\"}", "{\"owner-id\": \"123456\"}"})
    void givenInvalidTodo_whenPost_thenBadRequest(String body) throws Exception {

        mockMvc.perform(post("/todo")
                .with(ownerJwt)
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
                .with(ownerJwt)
                .contentType(APPLICATION_JSON)
                .content("{\"id\": 12, \"owner-id\": \"test_user\", \"description\": \"todo 1\", \"completed\": false}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.owner-id").value("test_user"))
                .andExpect(jsonPath("$.description").value("todo 1"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(todoService).update(12L, todo);
        verifyNoMoreInteractions(todoService);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "{\"completed\": true}", "{\"description\": \"todo\"}"})
    void givenInvalidTodo_whenUpdate_thenBadRequest(String body) throws Exception {

        mockMvc.perform(put("/todo/12")
                .with(ownerJwt)
                .contentType(APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenAnothersTodo_whenUpdate_thenForbidden() throws Exception {

        TodoDto todo = new TodoDto(12L, "test_user", "todo 1", false);
        when(todoService.update(12L, todo)).thenReturn(todo);

        mockMvc.perform(put("/todo/12")
                .accept(APPLICATION_JSON)
                .with(notOwnerJwt)
                .contentType(APPLICATION_JSON)
                .content("{\"id\": 12, \"owner-id\": \"test_user\", \"description\": \"todo 1\", \"completed\": false}"))
                .andExpect(status().isForbidden());

        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenTodoNotFound_whenUpdate_thenNotFound() throws Exception {

        TodoDto update = new TodoDto(12L, "test_user", "todo 1", false);
        when(todoService.update(12L, update)).thenThrow(TodoNotFoundException.class);

        mockMvc.perform(put("/todo/12")
                .accept(APPLICATION_JSON)
                .with(ownerJwt)
                .contentType(APPLICATION_JSON)
                .content("{\"id\": 12, \"owner-id\": \"test_user\", \"description\": \"todo 1\", \"completed\": false}"))
                .andExpect(status().isNotFound());

        verify(todoService).update(12L, update);
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
    void givenAnothersTodo_whenDelete_thenForbidden() throws Exception {

        TodoDto todo = new TodoDto(1L, "test_user", "todo 1", false);
        when(todoService.findById(1L)).thenReturn(todo);

        mockMvc.perform(delete("/todo/1")
                .with(notOwnerJwt))
                .andExpect(status().isForbidden());

        verify(todoService).findById(1L);
        verifyNoMoreInteractions(todoService);
    }

    @Test
    void givenNotFoundTodo_whenDelete_thenNotFound() throws Exception {

        when(todoService.findById(11L)).thenThrow(TodoNotFoundException.class);

        mockMvc.perform(delete("/todo/11")
                .with(ownerJwt))
                .andExpect(status().isNotFound());

        verify(todoService).findById(11L);
        verifyNoMoreInteractions(todoService);
    }
}