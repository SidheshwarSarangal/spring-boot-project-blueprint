package com.example.starter.cleanup;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class CleanupService {
    private final AtomicInteger runs = new AtomicInteger();

    public int expireOldItems() {
        return runs.incrementAndGet();
    }

    public int runCount() {
        return runs.get();
    }
}
