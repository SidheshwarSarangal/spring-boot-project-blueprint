# Process: Build a batch application

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this for large, finite, restartable imports, exports, reports, migrations, or transformations.

## Step 1 · Define one job and restart rule

> 📍 Create or edit `<project-root>/PROJECT.md`, section **5. Feature sheet**.

Record source, expected volume, item shape, validation, output, job parameters, chunk/transaction boundary, retry/skip/fail rules, restart rule, and audit counts.

Before continuing, check: You can state what happens to a bad record and how a failed job resumes without duplicating completed work.

Continue to Step 2.

## Step 2 · Generate and run foundation

> 📍 Open [Spring Initializr](https://start.spring.io/) in the browser. After extracting the project, open a terminal in `<project-root>/`. Put local input in `src/main/resources/input/` and local database settings in `src/main/resources/application-local.yml`.

Select Spring Batch, Actuator, and required database driver. Add Validation when item constraints use it.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Before continuing, check: Application starts and batch metadata storage initializes. Do not add `@EnableBatchProcessing` casually; Spring Boot auto-configuration should remain active unless intentionally replaced.

Continue to Step 3.

## Step 3 · Create reader, processor, writer, step, and job

> 📍 Under `src/main/java/com/company/project/`, create the `importjob/` folder and these files. Create the sample input file under `src/main/resources/input/`. Replace `com/company/project` with the package selected in Initializr.

```text
src/main/java/com/company/project/importjob/
├── ImportJobConfiguration.java
├── ImportItem.java
├── ImportItemProcessor.java
└── ImportJobListener.java
src/main/resources/input/sample.csv
```

Define reader/processor/writer beans appropriate to the source/target. Spring Batch 6 step skeleton:

```java
@Configuration
class ImportJobConfiguration {
    @Bean
    Step importStep(JobRepository jobRepository,
                    ItemReader<ImportItem> reader,
                    ItemProcessor<ImportItem, ImportItem> processor,
                    ItemWriter<ImportItem> writer) {
        return new ChunkOrientedStepBuilder<ImportItem, ImportItem>(
                "importStep", jobRepository, 100)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
    }

    @Bean
    Job importJob(JobRepository jobRepository, Step importStep) {
        return new JobBuilder("importJob", jobRepository)
            .start(importStep)
            .build();
    }
}
```

Use the builder API matching the Spring Batch version selected by Spring Boot; do not mix examples from an older major version.

Before continuing, check: Tiny valid input completes and reported read/write counts match expected output.

Continue to Step 4.

## Step 4 · Add validation, retry, skip, and restart

> 📍 Edit `src/main/java/com/company/project/importjob/ImportItemProcessor.java`, `ImportJobConfiguration.java`, and `ImportJobListener.java`. Write rejected-item output to the location recorded in `<project-root>/PROJECT.md`.

Validate/transform in processor; retry only transient failures; skip only explicitly acceptable bad items with a limit; fail on unknown/systemic errors; use identifying job parameters; keep reader/writer state restartable.

```java
@Component
class ImportItemProcessor implements ItemProcessor<ImportItem, ImportItem> {
    public ImportItem process(ImportItem item) {
        if (item.email() == null || item.email().isBlank()) {
            throw new InvalidImportItemException("email is required");
        }
        return item.normalized();
    }
}
```

Before continuing, check: One invalid input follows the chosen fail/skip rule; restart resumes correctly; repeating completed parameters does not duplicate output.

Continue to Step 5.

## Step 5 · Attach required capabilities and measure volume

> 📍 Edit `src/main/java/com/company/project/importjob/ImportJobConfiguration.java`. Then create only the required linked package under `src/main/java/com/company/project/`: feature persistence files, `provider/`, `file/`, or `messaging/`.

Add only required capabilities. Avoid unbounded per-record provider calls. Test realistic volume before adding concurrency; size database/HTTP pools consistently with any parallelism.

Before continuing, check: Realistic sample completes inside expected time/memory and produces auditable read/process/write/skip/fail counts.

Continue to Step 6.

## Step 6 · Test, operate, and deliver

> 📍 Create tests under `src/test/java/com/company/project/importjob/`. Edit `src/main/resources/application.yml`, the CI/deployment files in `<project-root>/`, and `<project-root>/README.md`. Run commands in `<project-root>/`.

Test empty/valid/malformed input, partial failure, retry/skip limit, restart, duplicate parameters, and realistic volume using the [testing guide](../docs/testing-guide.md). Document parameters, launch, duration, output, recovery, and monitoring.

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

Before continuing, check: Clean build passes; failed execution is restartable; operators can launch, observe, and recover the job.

Release, or return to Step 1 for another batch job.
