package com.mikhailkarpov.todoclient.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Todo {

    private Long id;

    @JsonProperty(value = "owner-id")
    private String ownerId;

    private String description;

    private Boolean completed;

}
