package com.mikhailkarpov.todoservice.todo;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CreateTodoRequest {

    @NotBlank
    private String description;

    @NotNull
    private Boolean completed;
}
