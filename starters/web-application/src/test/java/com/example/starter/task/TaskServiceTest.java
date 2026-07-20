package com.example.starter.task;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class TaskServiceTest {
    @Test
    void createsAndFindsTask() {
        TaskService service = new TaskService();
        long id = service.create(new TaskForm("Learn Spring"));
        assertThat(service.find(id).title()).isEqualTo("Learn Spring");
    }
}
