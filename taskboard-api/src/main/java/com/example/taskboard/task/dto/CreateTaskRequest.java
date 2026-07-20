package com.example.taskboard.task.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateTaskRequest(
        @NotBlank @Size(max = 120) String title,
        @Size(max = 1000) String description,
        @FutureOrPresent LocalDate dueDate
) {
}
