package com.mikhailkarpov.todoservice.todo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/{id}")
    @PostAuthorize("returnObject.ownerId == #jwt.subject")
    public TodoDto findById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {

        log.debug("Request for todo with id={} by sub={}", id, jwt.getSubject());
        return todoService.findById(id);
    }

    @PostMapping
    public ResponseEntity<TodoDto> save(@Valid @RequestBody CreateTodoRequest request,
                                        @AuthenticationPrincipal Jwt jwt,
                                        UriComponentsBuilder uriComponentsBuilder) {

        log.debug("Request to save todo={} from sub={}", request, jwt.getSubject());

        TodoDto dto = new TodoDto();
        dto.setOwnerId(jwt.getSubject());
        dto.setDescription(request.getDescription());
        dto.setCompleted(request.getCompleted());
        dto = todoService.save(dto);

        return ResponseEntity
                .created(uriComponentsBuilder.path("/todo/{id}").build(dto.getId()))
                .body(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("#update.ownerId == #jwt.subject")
    public TodoDto update(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody TodoDto update) {

        if (!id.equals(update.getId())) {
            throw new IllegalStateException("URI id and todo id don't match");
        }

        log.debug("Request to update todo={} from sub={}", update, jwt.getSubject());
        return todoService.update(id, update);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {

        log.debug("Request to delete todo with id={} by sub={}", id, jwt.getSubject());

        if (!todoService.findById(id).getOwnerId().equals(jwt.getSubject())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        todoService.delete(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}
