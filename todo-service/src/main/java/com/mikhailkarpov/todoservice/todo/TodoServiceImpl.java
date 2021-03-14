package com.mikhailkarpov.todoservice.todo;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    @Override
    @Transactional
    public void delete(Long id, String ownerId) {

        Todo todo = todoRepository.findById(id).orElseThrow(() -> {
            String message = String.format("Todo with id=%d not found", id);
            return new TodoNotFoundException(message);
        });
        todoRepository.delete(todo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoDto> findAllByOwnerId(String ownerId) {

        List<TodoDto> dtoList = new ArrayList<>();

        todoRepository.findAllByOwnerId(ownerId, Sort.by("description")).forEach(entity -> {
            TodoDto dto = mapFromEntity(entity);
            dtoList.add(dto);
        });

        return dtoList;
    }

    @Override
    @Transactional(readOnly = true)
    public TodoDto findById(Long id) {

        return todoRepository.findById(id).map(this::mapFromEntity).orElseThrow(() -> {
            String message = String.format("Todo with id={} not found");
            return new TodoNotFoundException(message);
        });
    }

    @Override
    @Transactional
    public TodoDto save(TodoDto dto) {

        Todo todo = new Todo();

        todo.setOwnerId(dto.getOwnerId());
        todo.setDescription(dto.getDescription());
        todo.setCompleted(dto.getCompleted());
        todo = todoRepository.save(todo);

        return mapFromEntity(todo);
    }

    @Override
    @Transactional
    public TodoDto update(Long id, TodoDto update) {

        Todo todo = todoRepository.findById(id).orElseThrow(() -> {
            String message = String.format("Todo with id=%d not found", id);
            return new TodoNotFoundException(message);
        });

        todo.setDescription(update.getDescription());
        todo.setCompleted(update.getCompleted());

        return mapFromEntity(todo);
    }

    private TodoDto mapFromEntity(Todo entity) {
        TodoDto dto = new TodoDto();

        dto.setId(entity.getId());
        dto.setOwnerId(entity.getOwnerId());
        dto.setDescription(entity.getDescription());
        dto.setCompleted(entity.getCompleted());

        return dto;
    }
}
