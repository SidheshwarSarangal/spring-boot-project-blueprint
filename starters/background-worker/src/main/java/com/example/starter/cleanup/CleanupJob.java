package com.example.starter.cleanup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CleanupJob {
    private static final Logger log = LoggerFactory.getLogger(CleanupJob.class);
    private final CleanupService service;

    public CleanupJob(CleanupService service) {
        this.service = service;
    }

    @Scheduled(cron = "${jobs.cleanup.cron}")
    void run() {
        log.info("cleanup completed; run={}", service.expireOldItems());
    }
}
