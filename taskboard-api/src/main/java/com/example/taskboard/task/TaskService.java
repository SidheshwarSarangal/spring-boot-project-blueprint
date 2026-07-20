package com.example.taskboard.task;

import com.example.taskboard.task.dto.CreateTaskRequest;
import com.example.taskboard.task.dto.TaskResponse;
import com.example.taskboard.task.dto.UpdateTaskRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;

    public TaskService(TaskRepository repository, TaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public TaskResponse create(CreateTaskRequest request) {
        Task task = Task.create(request.title(), request.description(), request.dueDate());
        return mapper.toResponse(repository.save(task));
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> findAll(TaskStatus status, Pageable pageable) {
        Page<Task> tasks = status == null
                ? repository.findAll(pageable)
                : repository.findAllByStatus(status, pageable);
        return tasks.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        return mapper.toResponse(requireTask(id));
    }

    @Transactional
    public TaskResponse update(Long id, UpdateTaskRequest request) {
        Task task = requireTask(id);
        task.update(request.title(), request.description(), request.status(), request.dueDate());
        return mapper.toResponse(repository.saveAndFlush(task));
    }

    @Transactional
    public void delete(Long id) {
        Task task = requireTask(id);
        repository.delete(task);
    }

    private Task requireTask(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }
}
