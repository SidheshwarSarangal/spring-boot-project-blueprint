package com.example.starter.task;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class TaskServiceTest {
    @Test
    void createsTask() {
        Task task = new TaskService().create(new CreateTaskInput("Learn GraphQL"));
        assertThat(task.title()).isEqualTo("Learn GraphQL");
    }
}
