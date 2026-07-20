package com.example.starter.command;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ImportCommand implements ApplicationRunner {
    private final ImportService service;

    public ImportCommand(ImportService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!args.containsOption("name")) {
            System.out.println("Usage: --name=<value>");
            return;
        }
        String name = args.getOptionValues("name").get(0);
        System.out.println(service.importFor(name));
    }
}
