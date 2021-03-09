package com.mikhailkarpov.todoservice.todo;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TodoRepository extends PagingAndSortingRepository<Todo, Long> {

    Optional<Todo> findByIdAndOwnerId(Long id, String ownerId);

    Iterable<Todo> findAllByOwnerId(String ownerId, Sort sort);
}
