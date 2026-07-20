package com.example.taskboard.task.dto;

import com.example.taskboard.task.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateTaskRequest(
        @NotBlank @Size(max = 120) String title,
        @Size(max = 1000) String description,
        @NotNull TaskStatus status,
        @FutureOrPresent LocalDate dueDate
) {
}
