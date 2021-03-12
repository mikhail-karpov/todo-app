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
    public TodoDto create(String subject, TodoDto dto) {

        Todo todo = new Todo();
        todo.setOwnerId(subject);
        todo.setDescription(dto.getDescription());
        todo.setCompleted(dto.getCompleted());
        todo = todoRepository.save(todo);

        return mapFromEntity(todo);
    }

    @Override
    @Transactional
    public void delete(Long id, String ownerId) {

        Todo todo = findByIdAndOwnerIdOrElseThrow(id, ownerId);
        todoRepository.delete(todo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoDto> findAllByOwnerId(String ownerId) {

        List<TodoDto> dtoList = new ArrayList<>();

        todoRepository.findAllByOwnerId(ownerId, Sort.by("description"))
                .forEach(entity -> {
                    TodoDto dto = mapFromEntity(entity);
                    dtoList.add(dto);
                });

        return dtoList;
    }

    @Override
    @Transactional
    public TodoDto update(String ownerId, TodoDto update) {

        Todo todo = findByIdAndOwnerIdOrElseThrow(update.getId(), ownerId);

        todo.setDescription(update.getDescription());
        todo.setCompleted(update.getCompleted());

        return mapFromEntity(todo);
    }

    private Todo findByIdAndOwnerIdOrElseThrow(Long id, String ownerId) {

        return todoRepository.findByIdAndOwnerId(id, ownerId).orElseThrow(() -> {
            return new TodoNotFoundException("Record not found");
        });
    }

    private TodoDto mapFromEntity(Todo entity) {
        TodoDto dto = new TodoDto();

        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setCompleted(entity.getCompleted());

        return dto;
    }
}
