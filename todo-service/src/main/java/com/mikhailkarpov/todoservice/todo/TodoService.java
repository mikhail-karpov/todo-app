package com.mikhailkarpov.todoservice.todo;

public interface TodoService {

    Todo create(Todo dto);

    void delete(Long id);

    Iterable<Todo> findAll();

    Todo findById(Long id);

    Todo update(Long id, Todo update);

}
