package com.example.starter.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class TaskService {
    private final AtomicLong ids = new AtomicLong();
    private final Map<Long, TaskView> tasks = new ConcurrentHashMap<>();

    public long create(TaskForm form) {
        long id = ids.incrementAndGet();
        tasks.put(id, new TaskView(id, form.title()));
        return id;
    }

    public TaskView find(long id) {
        TaskView task = tasks.get(id);
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        return task;
    }
}
