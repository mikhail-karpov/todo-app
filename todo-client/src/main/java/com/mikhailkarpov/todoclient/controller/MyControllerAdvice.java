package com.mikhailkarpov.todoclient.controller;

import com.mikhailkarpov.todoclient.exception.TodoServiceException;
import com.mikhailkarpov.todoclient.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class MyControllerAdvice {

    @ExceptionHandler(TodoServiceException.class)
    public ModelAndView handleTodoServiceException(TodoServiceException e) {

        HttpStatus status = e.getStatus();

        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", new ErrorResponse(status.value(), status.getReasonPhrase()));

        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e) {

        String message = "Something went wrong. Please, try again later";

        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", new ErrorResponse(500, message));

        return modelAndView;
    }
}
