package com.mikhailkarpov.todoservice.exception;

import com.mikhailkarpov.todoservice.todo.TodoNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class MyRestControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<Object> handleTodoNotFound(TodoNotFoundException e, WebRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponse errorResponse = new ErrorResponse(status, e.getMessage());
        return handleExceptionInternal(e, errorResponse, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {

        ErrorResponse errorResponse = new ValidationErrorResponse(status, ex);
        return handleExceptionInternal(ex, errorResponse, new HttpHeaders(), status, request);
    }
}
