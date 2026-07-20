# Process: Build a batch application

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this for large, finite, restartable imports, exports, reports, migrations, or transformations.

## Repository action map

| Step | Exact location | Add or run there |
|---|---|---|
| 1 | Create `<project-root>/PROJECT.md` | Job/input/output/restart contract |
| 2 | Browser: Initializr; Terminal: generated `<project-root>` | Generate/build/start Batch foundation |
| 3 | Create `src/main/java/com/company/project/importjob/`; add sample under `src/main/resources/input/` | Job/step/reader/processor/writer code |
| 4 | Edit processor and job/step fault-tolerance configuration | Validation/retry/skip/restart |
| 5 | Create/edit selected data/provider/file/messaging package | Real source/target and volume limits |
| 6 | Create matching `src/test/java/.../importjob/`; edit config/CI/operator README | Batch tests and delivery |

**Beginner actions by step:** 1 → [A workbook](../docs/beginner-execution-guide.md#action-a-create-the-working-repository-and-workbook); 2 → [B generate](../docs/beginner-execution-guide.md#action-b-generate-the-spring-project-in-the-browser), [D terminal](../docs/beginner-execution-guide.md#action-d-run-a-command-in-the-correct-terminal); 3–5 → [E create Java files](../docs/beginner-execution-guide.md#action-e-create-a-java-package-and-file), [F add code](../docs/beginner-execution-guide.md#action-f-put-a-provided-java-code-block-into-a-file), [I input resource](../docs/beginner-execution-guide.md#action-i-create-a-resource-file); 6 → [K tests](../docs/beginner-execution-guide.md#action-k-create-and-run-a-test), [M checkpoint](../docs/beginner-execution-guide.md#action-m-save-a-clean-checkpoint-with-git).

## Step 1 · Define one job and restart rule

**What:** Specify input, transformation, output, chunk/failure behavior, and completion report.

**Where:** One job feature sheet in `PROJECT.md`.

**Do:** Record source, expected volume, item shape, validation, output, job parameters, chunk/transaction boundary, retry/skip/fail rules, restart rule, and audit counts.

**Verify:** You can state what happens to a bad record and how a failed job resumes without duplicating completed work.

**Next:** Step 2.

## Step 2 · Generate and run foundation

**What:** Start Spring Batch with required metadata/output infrastructure.

**Where:** Spring Initializr, local input, and database configuration.

**Do:** Select Spring Batch, Actuator, and required database driver. Add Validation when item constraints use it.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

**Verify:** Application starts and batch metadata storage initializes. Do not add `@EnableBatchProcessing` casually; Spring Boot auto-configuration should remain active unless intentionally replaced.

**Next:** Step 3.

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

**Do:** Define reader/processor/writer beans appropriate to the source/target. Spring Batch 6 step skeleton:

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

**Verify:** Tiny valid input completes and reported read/write counts match expected output.

**Next:** Step 4.

## Step 4 · Add validation, retry, skip, and restart

**What:** Make partial failure behavior explicit and recoverable.

**Where:** Processor, step fault-tolerance configuration, job parameters, listener/error output.

**Do:** Validate/transform in processor; retry only transient failures; skip only explicitly acceptable bad items with a limit; fail on unknown/systemic errors; use identifying job parameters; keep reader/writer state restartable.

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

**Verify:** One invalid input follows the chosen fail/skip rule; restart resumes correctly; repeating completed parameters does not duplicate output.

**Next:** Step 5.

## Step 5 · Attach required capabilities and measure volume

**What:** Connect real source/target and confirm bounded performance.

**Where:** Selected [data](../capabilities/data-storage.md), [external API](../capabilities/external-api.md), [file](../capabilities/file-storage.md), or [messaging](../capabilities/messaging.md) package plus job configuration.

**Do:** Add only required capabilities. Avoid unbounded per-record provider calls. Test realistic volume before adding concurrency; size database/HTTP pools consistently with any parallelism.

**Verify:** Realistic sample completes inside expected time/memory and produces auditable read/process/write/skip/fail counts.

**Next:** Step 6.

## Step 6 · Test, operate, and deliver

**What:** Prove restart/failure behavior and provide an operator runbook.

**Where:** Batch tests, configuration, CI/deployment, project README.

**Do:** Test empty/valid/malformed input, partial failure, retry/skip limit, restart, duplicate parameters, and realistic volume using the [testing guide](../docs/testing-guide.md). Document parameters, launch, duration, output, recovery, and monitoring.

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

**Verify:** Clean build passes; failed execution is restartable; operators can launch, observe, and recover the job.

**Next:** Release, or return to Step 1 for another batch job.
