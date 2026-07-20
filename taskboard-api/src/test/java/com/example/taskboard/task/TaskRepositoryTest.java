package com.example.taskboard.task;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository repository;

    @Test
    void findsTasksByStatus() {
        Task saved = repository.saveAndFlush(
                Task.create("Read JPA docs", null, LocalDate.now().plusDays(1))
        );

        Page<Task> result = repository.findAllByStatus(TaskStatus.TODO, PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Task::getId)
                .contains(saved.getId());
    }
}
