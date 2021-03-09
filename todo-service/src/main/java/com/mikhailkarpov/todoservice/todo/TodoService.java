package com.mikhailkarpov.todoservice.todo;

import java.util.List;

public interface TodoService {

    TodoDto create(String ownerId, TodoDto todo);

    void delete(Long id, String ownerId);

    List<TodoDto> findAllByOwnerId(String ownerId);

    TodoDto update(String ownerId, TodoDto update);

}
