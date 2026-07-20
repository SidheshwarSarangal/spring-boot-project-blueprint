package com.example.taskboard.task;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Task " + id + " was not found");
    }
}
