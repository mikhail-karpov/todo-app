package com.mikhailkarpov.todoservice.todo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(String message) {
        super(message);
    }
}
