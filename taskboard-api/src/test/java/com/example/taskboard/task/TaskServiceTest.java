package com.example.taskboard.task;

import com.example.taskboard.task.dto.CreateTaskRequest;
import com.example.taskboard.task.dto.TaskResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @Test
    void createsATodoTask() {
        TaskMapper mapper = new TaskMapper();
        TaskService service = new TaskService(repository, mapper);
        CreateTaskRequest request = new CreateTaskRequest(
                "  Learn Spring  ",
                "Trace one request",
                LocalDate.now().plusDays(2)
        );
        when(repository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse result = service.create(request);

        assertThat(result.title()).isEqualTo("Learn Spring");
        assertThat(result.status()).isEqualTo(TaskStatus.TODO);
        verify(repository).save(any(Task.class));
    }

    @Test
    void reportsAMissingTask() {
        TaskService service = new TaskService(repository, new TaskMapper());
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task 99 was not found");
    }
}
