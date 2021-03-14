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
    void givenDto_whenSave_thenSuccess() {
        //given
        TodoDto dto = new TodoDto();
        dto.setDescription("description");
        dto.setCompleted(false);
        dto.setOwnerId("owner");

        Todo entity = new Todo("owner", "description", false);
        entity.setId(12L);

        //when
        when(todoRepository.save(any(Todo.class))).thenReturn(entity);
        TodoDto created = todoService.save(dto);

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
        when(todoRepository.findById(13L)).thenReturn(Optional.of(found));
        todoService.delete(13L, "owner");

        //then
        verify(todoRepository).findById(13L);
        verify(todoRepository).delete(any(Todo.class));
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    void givenNoTodo_whenDelete_thenThrows() {
        //when
        when(todoRepository.findById(13L)).thenReturn(Optional.empty());

        //then
        assertThrows(TodoNotFoundException.class, () -> todoService.delete(13L, "owner"));
        verify(todoRepository).findById(13L);
        verifyNoMoreInteractions(todoRepository);
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
        when(todoRepository.findById(13L)).thenReturn(Optional.of(found));
        TodoDto updated = todoService.update(13L, update);

        assertEquals(13L, updated.getId());
        assertEquals("update", updated.getDescription());
        assertFalse(updated.getCompleted());

        verify(todoRepository).findById(13L);
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    void givenTodoNotFound_whenUpdate_thenThrows() {
        //given
        TodoDto update = new TodoDto(13L, "update", false);

        //when
        when(todoRepository.findById(13L)).thenReturn(Optional.empty());

        //then
        assertThrows(TodoNotFoundException.class, () -> todoService.update(13L, update));
        verify(todoRepository).findById(13L);
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    void givenTodoFound_whenFindById_thenSuccess() {

        Todo todo = new Todo("owner", "update", false);
        todo.setId(13L);
        when(todoRepository.findById(13L)).thenReturn(Optional.of(todo));

        TodoDto found = todoService.findById(13L);

        assertEquals(13L, found.getId());
        assertEquals("owner", found.getOwnerId());
        assertEquals("update", found.getDescription());
        assertFalse(found.getCompleted());

        verify(todoRepository).findById(13L);
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    void givenTodoNotFound_whenFindById_thenThrows() {

        when(todoRepository.findById(13L)).thenReturn(Optional.empty());

        assertThrows(TodoNotFoundException.class, () -> todoService.findById(13L));
        verify(todoRepository).findById(13L);
        verifyNoMoreInteractions(todoRepository);
    }
}