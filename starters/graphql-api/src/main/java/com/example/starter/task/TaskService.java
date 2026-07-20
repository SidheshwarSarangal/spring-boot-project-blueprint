package com.example.starter.task;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private final AtomicLong ids = new AtomicLong();
    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();

    public Task create(CreateTaskInput input) {
        if (input.title() == null || input.title().isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        Task task = new Task(ids.incrementAndGet(), input.title());
        tasks.put(task.id(), task);
        return task;
    }

    public Task find(long id) {
        return tasks.get(id);
    }

    public List<Task> findAll() {
        return tasks.values().stream().toList();
    }
}
