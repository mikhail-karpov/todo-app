package com.mikhailkarpov.todoservice.todo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Entity(name = "Todo")
@Table(name = "todos")
@Getter
@Setter
public class Todo {

    @Id
    @SequenceGenerator(name = "todos_id_seq", sequenceName = "todos_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "todos_id_seq")
    private Long id;

    @Column(name = "owner_id", nullable = false)
    private String ownerId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "completed", nullable = false)
    private Boolean completed;

    protected Todo() {
        //for JPA and JSON mapping
    }

    public Todo(String description, Boolean completed) {
        this.description = description;
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", completed=" + completed +
                '}';
    }
}
