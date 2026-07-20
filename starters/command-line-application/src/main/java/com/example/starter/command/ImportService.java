package com.example.starter.command;

import org.springframework.stereotype.Service;

@Service
public class ImportService {
    public String importFor(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        return "Imported data for " + name;
    }
}
