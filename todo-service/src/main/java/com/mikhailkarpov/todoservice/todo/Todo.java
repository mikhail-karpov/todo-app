package com.mikhailkarpov.todoservice.todo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity(name = "Todo")
@Table(name = "todos")
@Getter
@Setter
public class Todo {

    @Id
    @SequenceGenerator(name = "todos_id_seq", sequenceName = "todos_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "todos_id_seq")
    private Long id;

    @Column(name = "description", nullable = false)
    @NotBlank
    private String description;

    @Column(name = "completed", nullable = false)
    @NotNull
    private Boolean completed;

    protected Todo() {

    }

    public Todo(String description) {
        this(description, false);
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
