package com.mikhailkarpov.todoservice.todo;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@RestController
@RequestMapping("/todo")
@AllArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public Iterable<Todo> findAll() {

        return todoService.findAll();
    }

    @GetMapping("/{id}")
    public Todo findById(@PathVariable Long id) {

        return todoService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Todo> save(@Valid @RequestBody Todo todo,
                                     UriComponentsBuilder uriComponentsBuilder) {

        Todo saved = todoService.create(todo);

        return ResponseEntity
                .created(uriComponentsBuilder.path("/todo/{id}").build(saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public Todo update(@PathVariable Long id, @Valid @RequestBody Todo update) {

        return todoService.update(id, update);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {

        todoService.delete(id);
    }
}
