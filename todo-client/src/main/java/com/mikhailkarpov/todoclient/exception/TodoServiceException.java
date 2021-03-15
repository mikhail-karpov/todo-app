package com.mikhailkarpov.todoclient.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class TodoServiceException extends RuntimeException {

    private final HttpStatus status;
}
