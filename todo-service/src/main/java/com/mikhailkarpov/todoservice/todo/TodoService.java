package com.mikhailkarpov.todoservice.todo;

import java.util.List;

public interface TodoService {

    void delete(Long id, String ownerId);

    List<TodoDto> findAllByOwnerId(String ownerId);

    TodoDto findById(Long id);

    TodoDto save(TodoDto todo);

    TodoDto update(Long id, TodoDto update);

}
