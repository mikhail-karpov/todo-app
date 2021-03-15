package com.mikhailkarpov.todoservice.todo;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TodoRepository extends PagingAndSortingRepository<Todo, Long> {

    Iterable<Todo> findAllByOwnerId(String ownerId, Sort sort);
}
