package com.example.taskboard.task.dto;

import com.example.taskboard.task.TaskStatus;

import java.time.Instant;
import java.time.LocalDate;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        LocalDate dueDate,
        Instant createdAt,
        Instant updatedAt,
        Long version
) {
}
