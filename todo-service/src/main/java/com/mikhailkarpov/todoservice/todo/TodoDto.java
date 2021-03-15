package com.mikhailkarpov.todoservice.todo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
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

    @JsonProperty("owner-id")
    private String ownerId;

    @NotBlank
    private String description;

    @NotNull
    private Boolean completed;

    public TodoDto(Long id, @NotBlank String description, @NotNull Boolean completed) {
        this.id = id;
        this.description = description;
        this.completed = completed;
    }
}
