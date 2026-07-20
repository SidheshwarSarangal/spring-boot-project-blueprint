package com.example.starter.task;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping("/new")
    String form(Model model) {
        model.addAttribute("taskForm", new TaskForm(""));
        return "tasks/form";
    }

    @PostMapping
    String create(@Valid @ModelAttribute TaskForm taskForm, BindingResult errors) {
        if (errors.hasErrors()) {
            return "tasks/form";
        }
        return "redirect:/tasks/" + service.create(taskForm);
    }

    @GetMapping("/{id}")
    String detail(@PathVariable long id, Model model) {
        model.addAttribute("task", service.find(id));
        return "tasks/detail";
    }
}
