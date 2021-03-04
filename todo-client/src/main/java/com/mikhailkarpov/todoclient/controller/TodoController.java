package com.mikhailkarpov.todoclient.controller;

import com.mikhailkarpov.todoclient.model.Todo;
import com.mikhailkarpov.todoclient.model.TodoForm;
import com.mikhailkarpov.todoclient.service.TodoService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("todoForm", new TodoForm());

        return "todo";
    }

    @PostMapping("/todo")
    public String saveTodo(@Valid @ModelAttribute TodoForm todoForm,
                           BindingResult result,
                           @AuthenticationPrincipal OAuth2User user,
                           RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            String message = result.getAllErrors().stream().findFirst().get().getDefaultMessage();
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/todo";
        }

        log.debug("Request to save todo: {} by '{}'", todoForm, user.getName());

        Todo todo = new Todo();
        todo.setDescription(todoForm.getDescription());
        todo.setCompleted(false);
        todoService.save(todo);

        return "redirect:/todo";
    }

    @PostMapping("/todo/update")
    public String toggleComplete(@RequestParam Long id,
                                 @RequestParam String description,
                                 @RequestParam Boolean completed,
                                 @AuthenticationPrincipal OAuth2User user) {

        log.debug("Request to toggle complete in todo with id={} by '{}'", id, user.getName());

        Todo update = new Todo(id, description, !completed);
        todoService.update(id, update);

        return "redirect:/todo";
    }

    @PostMapping("/todo/delete")
    public String deleteTodo(@RequestParam Long id, @AuthenticationPrincipal OAuth2User user) {

        log.debug("Request to delete todo with id={} by '{}'", id, user.getName());

        todoService.delete(id);
        return "redirect:/todo";
    }
}
