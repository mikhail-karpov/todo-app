package com.mikhailkarpov.todoclient.service;

import com.mikhailkarpov.todoclient.model.Todo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class TodoServiceImpl implements TodoService {

    private static final AtomicLong nextId = new AtomicLong(3);

    private final List<Todo> todoList = new CopyOnWriteArrayList<>(Arrays.asList(
            new Todo(1L, "first todo", true),
            new Todo(2L, "second todo", false)
    ));

    @Override
    public List<Todo> findByUserName(String name) {
        log.debug("Loading todo list");
        return todoList;
    }

    @Override
    public void save(Todo todo) {
        todo.setId(nextId.getAndIncrement());
        todoList.add(todo);
        log.debug("Saving {}", todo);
    }

    @Override
    public void update(Long id, Todo update) {

        boolean found = false;
        Iterator<Todo> iterator = todoList.iterator();

        while (iterator.hasNext() || !found) {
            Todo next = iterator.next();

            if (next.getId().equals(id)) {
                next.setCompleted(update.getCompleted());
                next.setDescription(update.getDescription());

                log.debug("Updated {}", next);
                found = true;
            }
        }

        if (!found) {
            String errorMessage = String.format("Todo with id=%d not found", id);
            log.error(errorMessage);
        }
    }

    @Override
    public void delete(Long id) {

        Todo toBeRemoved = null;
        Iterator<Todo> iterator = todoList.iterator();

        while (iterator.hasNext() || toBeRemoved == null) {
            Todo next = iterator.next();
            if (next.getId().equals(id)) {
                toBeRemoved = next;
            }
        }

        if (toBeRemoved != null) {
            todoList.remove(toBeRemoved);
            log.debug("Todo removed");

        } else {

            String errorMessage = String.format("Todo with id=%d not found", id);
            log.error(errorMessage);
        }
    }
}
