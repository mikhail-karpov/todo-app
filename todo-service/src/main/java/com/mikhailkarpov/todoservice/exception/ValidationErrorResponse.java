package com.mikhailkarpov.todoservice.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationErrorResponse extends ErrorResponse {

    @JsonProperty(value = "errors")
    private final List<ValidationError> validationErrors;

    public ValidationErrorResponse(HttpStatus status, MethodArgumentNotValidException e) {
        super(status, "Validation failed");
        this.validationErrors = buildValidationErrors(e);
    }

    private List<ValidationError> buildValidationErrors(MethodArgumentNotValidException e) {
        List<ValidationError> validationErrors = new ArrayList<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            String field = error.getField();
            Object rejectedValue = error.getRejectedValue();
            String message = error.getDefaultMessage();

            validationErrors.add(new ValidationError(field, rejectedValue, message));
        }

        return validationErrors;
    }

    @Data
    private static class ValidationError {

        private final String field;

        @JsonProperty(value = "rejected_value")
        private final Object rejectedValue;

        private final String message;
    }
}
