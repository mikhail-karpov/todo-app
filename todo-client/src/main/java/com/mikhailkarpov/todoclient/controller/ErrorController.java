package com.mikhailkarpov.todoclient.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e) {

        return new ModelAndView("error");
    }
}
