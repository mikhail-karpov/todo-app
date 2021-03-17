package com.mikhailkarpov.todoservice.todo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class TodoDtoTest {

    @Autowired
    private JacksonTester<TodoDto> jacksonTester;

    @Test
    void testSerialize() throws IOException {
        TodoDto todoDto = new TodoDto(10L, "ownerId", "todo 1", true);
        String expectedJson = "{\"id\": 10, \"owner-id\": \"ownerId\", \"description\": \"todo 1\", \"completed\": true}";

        assertThat(jacksonTester.write(todoDto)).isEqualToJson(expectedJson);
    }

    @Test
    void testDeserialize() throws IOException {
        String json = "{\"id\": 10, \"owner-id\": \"ownerId\", \"description\": \"todo 1\", \"completed\": true}";
        TodoDto expectedDto = new TodoDto(10L, "ownerId", "todo 1", true);

        assertThat(jacksonTester.parse(json)).usingRecursiveComparison().isEqualTo(expectedDto);
    }
}