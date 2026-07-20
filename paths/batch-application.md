# Process: Build a batch application

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this for large, finite, restartable imports, exports, reports, migrations, or transformations.

## Step 1 · Define one job and restart rule

**What:** Specify input, transformation, output, chunk/failure behavior, and completion report.

**Where:** Create or edit `<project-root>/PROJECT.md`, section **5. Feature sheet**.

**Do now:** Record source, expected volume, item shape, validation, output, job parameters, chunk/transaction boundary, retry/skip/fail rules, restart rule, and audit counts.

**Finish this step when:** You can state what happens to a bad record and how a failed job resumes without duplicating completed work.

**Go next:** Step 2.

## Step 2 · Generate and run foundation

**What:** Start Spring Batch with required metadata/output infrastructure.

**Where:** Browser at `https://start.spring.io`; terminal at `<project-root>`; local input under `src/main/resources/input/`; database settings in `src/main/resources/application-local.yml`.

**Do now:** Select Spring Batch, Actuator, and required database driver. Add Validation when item constraints use it.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

**Finish this step when:** Application starts and batch metadata storage initializes. Do not add `@EnableBatchProcessing` casually; Spring Boot auto-configuration should remain active unless intentionally replaced.

**Go next:** Step 3.

## Step 3 · Create reader, processor, writer, step, and job

**What:** Execute one small input through chunk processing.

**Where:**

```text
src/main/java/com/company/project/importjob/
├── ImportJobConfiguration.java
├── ImportItem.java
├── ImportItemProcessor.java
└── ImportJobListener.java
src/main/resources/input/sample.csv
```

**Do now:** Define reader/processor/writer beans appropriate to the source/target. Spring Batch 6 step skeleton:

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

**Finish this step when:** Tiny valid input completes and reported read/write counts match expected output.

**Go next:** Step 4.

## Step 4 · Add validation, retry, skip, and restart

**What:** Make partial failure behavior explicit and recoverable.

**Where:** Edit `src/main/java/com/company/project/importjob/ImportItemProcessor.java` and `ImportJobConfiguration.java`; create/edit `ImportJobListener.java` and the error-output location documented in `PROJECT.md`.

**Do now:** Validate/transform in processor; retry only transient failures; skip only explicitly acceptable bad items with a limit; fail on unknown/systemic errors; use identifying job parameters; keep reader/writer state restartable.

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

**Finish this step when:** One invalid input follows the chosen fail/skip rule; restart resumes correctly; repeating completed parameters does not duplicate output.

**Go next:** Step 5.

## Step 5 · Attach required capabilities and measure volume

**What:** Connect real source/target and confirm bounded performance.

**Where:** Edit `importjob/ImportJobConfiguration.java`; then create only the required linked folder: `data`/feature persistence files, `provider/`, `file/`, or `messaging/`.

**Do now:** Add only required capabilities. Avoid unbounded per-record provider calls. Test realistic volume before adding concurrency; size database/HTTP pools consistently with any parallelism.

**Finish this step when:** Realistic sample completes inside expected time/memory and produces auditable read/process/write/skip/fail counts.

**Go next:** Step 6.

## Step 6 · Test, operate, and deliver

**What:** Prove restart/failure behavior and provide an operator runbook.

**Where:** Create tests under `src/test/java/com/company/project/importjob/`; edit `application.yml`, root CI/deployment files, and `<project-root>/README.md`; run commands at `<project-root>`.

**Do now:** Test empty/valid/malformed input, partial failure, retry/skip limit, restart, duplicate parameters, and realistic volume using the [testing guide](../docs/testing-guide.md). Document parameters, launch, duration, output, recovery, and monitoring.

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

**Finish this step when:** Clean build passes; failed execution is restartable; operators can launch, observe, and recover the job.

**Go next:** Release, or return to Step 1 for another batch job.
