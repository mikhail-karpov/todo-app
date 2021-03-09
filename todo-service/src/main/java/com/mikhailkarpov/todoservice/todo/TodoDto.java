package com.mikhailkarpov.todoservice.todo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoDto {

    private Long id;

    @NotBlank
    private String description;

    @NotNull
    private Boolean completed;
}
