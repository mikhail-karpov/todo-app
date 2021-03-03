package com.mikhailkarpov.todoclient.service;


import com.mikhailkarpov.todoclient.model.Todo;

import java.util.List;

public interface TodoService {

    List<Todo> findByUserName(String name);

    void save(Todo todo);

    void update(Long id, Todo update);

    void delete(Long id);
}
