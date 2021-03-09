package com.mikhailkarpov.todoservice.todo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/todo")
@AllArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public List<TodoDto> findAllByOwner(@AuthenticationPrincipal Jwt jwt) {

        log.debug("Request for todo list with sub={}", jwt.getSubject());
        return todoService.findAllByOwnerId(jwt.getSubject());
    }

    @PostMapping
    public ResponseEntity<TodoDto> save(@Valid @RequestBody TodoDto todo,
                                        @AuthenticationPrincipal Jwt jwt,
                                        UriComponentsBuilder uriComponentsBuilder) {

        log.debug("Request to save todo={} from sub={}", todo, jwt.getSubject());
        TodoDto saved = todoService.create(jwt.getSubject(), todo);

        return ResponseEntity
                .created(uriComponentsBuilder.path("/todo/{id}").build(saved.getId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    public TodoDto update(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody TodoDto update) {

        if (!id.equals(update.getId())) {
            throw new IllegalStateException("URI id and todo id don't match");
        }

        log.debug("Request to update todo={} from sub={}", update, jwt.getSubject());
        return todoService.update(jwt.getSubject(), update);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {

        todoService.delete(id, jwt.getSubject());
    }
}
