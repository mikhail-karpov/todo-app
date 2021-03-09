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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final TodoDto invalid1 = new TodoDto(1L, null, true);

    private final TodoDto invalid2 = new TodoDto(2L, "description", null);

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
    void givenTodoList_whenGet_thenOk() throws Exception {
        //when
        when(todoService.findAllByOwnerId("test_user")).thenReturn(Arrays.asList(
                new TodoDto(1L, "todo 1", true),
                new TodoDto(2L, "todo 2", false)
        ));

        mockMvc.perform(get("/todo")
                .with(jwt().jwt(jwt -> jwt.subject("test_user")))
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("todo 1"))
                .andExpect(jsonPath("$[0].completed").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].description").value("todo 2"))
                .andExpect(jsonPath("$[1].completed").value(false));

        //given empty list
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
    void givenValidTodo_whenPost_thenCreated() throws Exception {
        //given
        TodoDto request = new TodoDto();
        request.setDescription("todo 1");
        request.setCompleted(true);

        TodoDto response = new TodoDto();
        response.setId(12L);
        response.setDescription("todo 1");
        response.setCompleted(true);

        //when
        when(todoService.create("test_user", request)).thenReturn(response);

        //then
        mockMvc.perform(post("/todo")
                .with(jwt().jwt(jwt -> jwt.subject("test_user")))
                .contentType(APPLICATION_JSON)
                .content("{\"description\": \"todo 1\", \"completed\": true}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/todo/12"))
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.description").value("todo 1"))
                .andExpect(jsonPath("$.completed").value(true));

        verify(todoService).create("test_user", request);
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
    void givenValidTodo_whenUpdate_thenOk() throws Exception {
        TodoDto todo = new TodoDto(12L, "todo 1", true);

        //when
        when(todoService.update("test_user", todo)).thenReturn(todo);

        //then
        mockMvc.perform(put("/todo/12")
                .with(jwt().jwt(jwt -> jwt.subject("test_user")))
                .contentType(APPLICATION_JSON)
                .content("{\"id\": 12, \"description\": \"todo 1\", \"completed\": true}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.description").value("todo 1"))
                .andExpect(jsonPath("$.completed").value(true));

        verify(todoService).update("test_user", todo);
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
    void whenDelete_thenNoContent() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/todo/1")
                .with(jwt().jwt(jwt -> jwt.subject("test_user"))))
                .andExpect(status().isNoContent());

        verify(todoService).delete(1L, "test_user");
        verifyNoMoreInteractions(todoService);
    }
}