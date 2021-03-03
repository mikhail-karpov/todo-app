package com.mikhailkarpov.todoclient.controller;

import com.mikhailkarpov.todoclient.model.Todo;
import com.mikhailkarpov.todoclient.model.TodoForm;
import com.mikhailkarpov.todoclient.service.TodoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@AllArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/todo")
    public String todoList(Model model, @AuthenticationPrincipal OAuth2User user) {

        log.debug("Request for todo list by '{}'", user.getName());

        List<Todo> todoList = todoService.findByUserName(user.getName());
        model.addAttribute("todoList", todoList);
        model.addAttribute("todo", new TodoForm());

        return "todo";
    }

    @PostMapping("/todo")
    public String saveTodo(@Valid @ModelAttribute TodoForm todoForm,
                           BindingResult result,
                           @AuthenticationPrincipal OAuth2User user) {

        if (result.hasErrors()) {
            return "todo";
        }

        log.debug("Request to save todo: {} by '{}'", todoForm, user.getName());

        Todo todo = new Todo();
        todo.setDescription(todoForm.getDescription());
        todo.setCompleted(false);

        todoService.save(todo);
        return "redirect:/todo";
    }

    @PostMapping("/todo/toggleComplete")
    public String toggleComplete(@Valid @ModelAttribute Todo todo, @AuthenticationPrincipal OAuth2User user) {

        log.debug("Request to toggle complete for todo with id={} by '{}'", todo.getId(), user.getName());
        if (todo.getCompleted()) {
            todo.setCompleted(false);
        } else {
            todo.setCompleted(true);
        }

        todoService.update(todo.getId(), todo);
        return "redirect:/todo";
    }

    @PostMapping("/todo/delete")
    public String deleteTodo(@RequestParam Long id, @AuthenticationPrincipal OAuth2User user) {

        log.debug("Request to delete todo with id={} by '{}'", id, user.getName());

        todoService.delete(id);
        return "redirect:/todo";
    }
}
