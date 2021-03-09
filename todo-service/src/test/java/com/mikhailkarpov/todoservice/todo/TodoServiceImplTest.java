package com.mikhailkarpov.todoservice.todo;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TodoServiceImplTest {

    private final TodoRepository todoRepository = mock(TodoRepository.class);

    private final TodoService todoService = new TodoServiceImpl(todoRepository);

    @Test
    void givenDto_whenCreate_thenSuccess() {
        //given
        TodoDto dto = new TodoDto();
        dto.setDescription("description");
        dto.setCompleted(false);

        Todo entity = new Todo();
        entity.setId(12L);
        entity.setOwnerId("owner");
        entity.setDescription("description");
        entity.setCompleted(false);

        //when
        when(todoRepository.save(any(Todo.class))).thenReturn(entity);
        TodoDto created = todoService.create("owner", dto);

        //then
        assertEquals(12L, created.getId());
        assertEquals("description", created.getDescription());
        assertFalse(created.getCompleted());

        verify(todoRepository).save(any(Todo.class));
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    void givenTodo_whenDelete_thenOk() {
        //given
        Todo found = new Todo();
        found.setId(12L);
        found.setOwnerId("owner");
        found.setDescription("todo 1");
        found.setCompleted(true);

        //when
        when(todoRepository.findByIdAndOwnerId(13L, "owner")).thenReturn(Optional.of(found));
        todoService.delete(13L, "owner");

        //then
        verify(todoRepository).findByIdAndOwnerId(13L, "owner");
        verify(todoRepository).delete(any(Todo.class));
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    void givenNoTodo_whenDelete_thenThrows() {
        //when
        when(todoRepository.findByIdAndOwnerId(13L, "owner")).thenReturn(Optional.empty());

        //then
        assertThrows(TodoNotFoundException.class, () -> todoService.delete(13L, "owner"));
    }

    @Test
    void givenTodoList_whenFindAllByOwnerId_thenFound() {
        //when
        when(todoRepository.findAllByOwnerId(any(String.class), any(Sort.class)))
                .thenReturn(Arrays.asList(new Todo(), new Todo()));

        List<TodoDto> dtoList = todoService.findAllByOwnerId("ownerId");

        assertEquals(2, dtoList.size());
        verify(todoRepository).findAllByOwnerId("ownerId", Sort.by("description"));
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    void givenTodo_whenUpdate_thenUpdated() {
        //given
        TodoDto update = new TodoDto(13L, "update", false);

        Todo found = new Todo();
        found.setId(13L);
        found.setDescription("description");
        found.setOwnerId("ownerId");
        found.setCompleted(true);

        //when
        when(todoRepository.findByIdAndOwnerId(13L, "owner")).thenReturn(Optional.of(found));
        TodoDto updated = todoService.update("owner", update);

        assertEquals(13L, updated.getId());
        assertEquals("update", updated.getDescription());
        assertFalse(updated.getCompleted());

        verify(todoRepository).findByIdAndOwnerId(13L, "owner");
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    void givenTodoNotFound_whenUpdate_thenThrows() {
        //given
        TodoDto update = new TodoDto(13L, "update", false);

        //when
        when(todoRepository.findByIdAndOwnerId(13L, "owner")).thenReturn(Optional.empty());

        //then
        assertThrows(TodoNotFoundException.class, () -> todoService.update("owner", update));
    }
}