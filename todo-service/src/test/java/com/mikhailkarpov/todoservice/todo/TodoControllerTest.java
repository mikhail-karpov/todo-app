package com.mikhailkarpov.todoservice.todo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Todo invalid1 = new Todo(null, true);
    private final Todo invalid2 = new Todo("description", null);

    @Test
    void givenNoToken_whenHitEndpoints_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/todo"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/todo/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/todo/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(MockMvcRequestBuilders.delete("/todo/1"))
                .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(todoService);
    }

    @Test
    @WithMockUser
    void givenTodo_whenGetById_thenOk() throws Exception {
        //given
        Todo todo = new Todo("todo 1", true);
        todo.setId(10L);

        when(todoService.findById(10L)).thenReturn(todo);

        mockMvc.perform(get("/todo/10")
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.description").value("todo 1"))
                .andExpect(jsonPath("$.completed").value(true));

        verify(todoService).findById(10L);
        verifyNoMoreInteractions(todoService);
    }

    @Test
    @WithMockUser
    void givenTodoList_whenGet_thenOk() throws Exception {
        //given not empty list
        when(todoService.findAll()).thenReturn(Arrays.asList(
                new Todo("todo 1", true),
                new Todo("todo 2", false)
        ));

        mockMvc.perform(get("/todo")
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description").value("todo 1"))
                .andExpect(jsonPath("$[0].completed").value(true))
                .andExpect(jsonPath("$[1].description").value("todo 2"))
                .andExpect(jsonPath("$[1].completed").value(false));

        //given empty list
        when(todoService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/todo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(todoService, times(2)).findAll();
        verifyNoMoreInteractions(todoService);
    }

    @Test
    @WithMockUser
    void givenValidTodo_whenPost_thenCreated() throws Exception {
        //given
        Todo todo = new Todo("todo 1", false);
        todo.setId(12L);

        //when
        when(todoService.create(any(Todo.class))).thenReturn(todo);

        //then
        mockMvc.perform(post("/todo")
                .contentType(APPLICATION_JSON)
                .content("{\"description\": \"todo 1\", \"completed\": true}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/todo/12"))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.description").value("todo 1"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(todoService).create(any());
        verifyNoMoreInteractions(todoService);
    }

    @Test
    @WithMockUser
    void givenInvalidTodo_whenPost_thenBadRequest() throws Exception {

        mockMvc.perform(post("/todo")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid1)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/todo")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid2)))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(todoService);
    }

    @Test
    @WithMockUser
    void givenValidTodo_whenUpdate_thenOk() throws Exception {
        Todo todo = new Todo("todo 1", true);
        todo.setId(12L);

        //when
        when(todoService.update(any(Long.class), any(Todo.class))).thenReturn(todo);

        //then
        mockMvc.perform(put("/todo/12")
                .contentType(APPLICATION_JSON)
                .content("{\"description\": \"todo 1\", \"completed\": true}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.description").value("todo 1"))
                .andExpect(jsonPath("$.completed").value(true));

        verify(todoService).update(any(), any());
        verifyNoMoreInteractions(todoService);
    }

    @Test
    @WithMockUser
    void givenInvalidTodo_whenUpdate_thenBadRequest() throws Exception {

        mockMvc.perform(put("/todo/12")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid1)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/todo/12")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid2)))
                .andExpect(status().isBadRequest());

        verifyNoMoreInteractions(todoService);
    }

    @Test
    @WithMockUser
    void whenDelete_thenNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/todo/1"))
                .andExpect(status().isNoContent());

        verify(todoService).delete(1L);
        verifyNoMoreInteractions(todoService);
    }
}