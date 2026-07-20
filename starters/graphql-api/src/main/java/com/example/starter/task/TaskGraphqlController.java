package com.example.starter.task;

import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class TaskGraphqlController {
    private final TaskService service;

    public TaskGraphqlController(TaskService service) {
        this.service = service;
    }

    @QueryMapping
    Task task(@Argument long id) {
        return service.find(id);
    }

    @QueryMapping
    List<Task> tasks() {
        return service.findAll();
    }

    @MutationMapping
    Task createTask(@Argument CreateTaskInput input) {
        return service.create(input);
    }
}
