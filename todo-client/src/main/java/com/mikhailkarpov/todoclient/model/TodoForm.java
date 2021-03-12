package com.mikhailkarpov.todoclient.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class TodoForm {

    @Size(min = 2, max = 255, message = "Description must be at least 2 and at most 255 characters long")
    private String description;
}
