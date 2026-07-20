package com.example.starter.importjob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class ImportJobConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ImportJobConfiguration.class);

    @Bean
    Step importStep(JobRepository jobs, PlatformTransactionManager transactions,
                    ImportService service) {
        return new StepBuilder("import-step", jobs)
            .tasklet((contribution, context) -> {
                log.info("imported {} sample items", service.importSample());
                return RepeatStatus.FINISHED;
            }, transactions)
            .build();
    }

    @Bean
    Job importJob(JobRepository jobs, Step importStep) {
        return new JobBuilder("import-job", jobs).start(importStep).build();
    }
}
