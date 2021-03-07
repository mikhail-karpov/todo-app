package com.mikhailkarpov.todoclient.service;

import com.mikhailkarpov.todoclient.model.Todo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TodoServiceImpl implements TodoService {

    @Value("${app.services.todo-service.uri}")
    private String todoServiceUri;

    private final WebClient webClient;

    public TodoServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<Todo> findByUserName(String name) {

        ResponseEntity<Flux<Todo>> responseEntity = webClient.get()
                .uri(todoServiceUri)
                .retrieve()
                .toEntityFlux(Todo.class)
                .block();

        log.debug("GET {} resulted in {}", todoServiceUri, responseEntity.getStatusCode());

        List<Todo> todoList = new ArrayList<>();
        responseEntity.getBody().toIterable().forEach(todoList::add);
        return todoList;
    }

    @Override
    public void save(Todo todo) {

        ResponseEntity<Todo> responseEntity = webClient.post()
                .uri(todoServiceUri)
                .body(BodyInserters.fromValue(todo))
                .retrieve()
                .toEntity(Todo.class)
                .block();

        log.debug("POST {} with body={} resulted in {}", todoServiceUri, todo, responseEntity.getStatusCode());
    }

    @Override
    public void update(Long id, Todo update) {

        String uri = todoServiceUri + "/" + id;

        ResponseEntity<Todo> responseEntity = webClient.put()
                .uri(uri)
                .body(BodyInserters.fromValue(update))
                .retrieve()
                .toEntity(Todo.class)
                .block();

        log.debug("PUT {} with body={} resulted in {}", uri, update, responseEntity.getStatusCode());
    }

    @Override
    public void delete(Long id) {

        String uri = todoServiceUri + "/" + id;

        ResponseEntity<Void> responseEntity = webClient.delete()
                .uri(uri)
                .retrieve()
                .toBodilessEntity()
                .block();

        log.debug("DELETE {} resulted in {}", uri, responseEntity.getStatusCode());
    }
}
