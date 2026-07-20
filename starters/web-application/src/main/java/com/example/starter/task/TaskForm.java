package com.example.starter.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskForm(@NotBlank @Size(max = 120) String title) {
}
