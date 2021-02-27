package com.mikhailkarpov.todoservice.todo;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    @Transactional
    public Todo create(Todo todoDto) {

        Todo entity = new Todo(todoDto.getDescription(), todoDto.getCompleted());
        return todoRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {

        Todo todo = findByIdOrElseThrow(id);
        todoRepository.delete(todo);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<Todo> findAll() {

        return todoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Todo findById(Long id) {

        return findByIdOrElseThrow(id);
    }

    @Override
    @Transactional
    public Todo update(Long id, Todo update) {

        Todo todo = findByIdOrElseThrow(id);

        todo.setDescription(update.getDescription());
        todo.setCompleted(update.getCompleted());

        return todo;
    }

    private Todo findByIdOrElseThrow(Long id) {

        return todoRepository.findById(id).orElseThrow(() -> {
            String message = String.format("Record with id=%d not found", id);
            return new TodoNotFoundException(message);
        });
    }
}
