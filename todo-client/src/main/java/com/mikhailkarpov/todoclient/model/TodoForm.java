package com.mikhailkarpov.todoclient.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class TodoForm {

    @NotBlank(message = "Todo must be provided")
    private String description;
}
