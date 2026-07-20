package com.example.starter.order;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public boolean process(String eventId) {
        return processedEventIds.add(eventId);
    }

    public int processedCount() {
        return processedEventIds.size();
    }
}
