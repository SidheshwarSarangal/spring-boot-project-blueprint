# Path: Batch application

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once before Step 1.

Choose this for large finite imports, exports, reports, migrations, or processing that must restart safely.

## 1. Define the job

```text
Input source and expected volume:
Output destination:
Record validation/transformation:
Chunk and transaction boundary:
Skip/retry policy:
Restart rule:
Success report:
```

Record it in the [project workbook](../docs/project-workbook.md).

## 2. Generate the project

Select Spring Batch, Actuator, and the required database driver. Add Validation when input objects use Jakarta constraints.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Continue only after the untouched application and batch metadata database initialize successfully.

## 3. Build one job

```text
Job → Step → ItemReader → ItemProcessor → ItemWriter
                  ↘ job repository/status ↙
```

```text
src/main/java/com/company/project/importjob/
├── ImportJobConfiguration.java
├── ImportItem.java
├── ImportItemReader.java
├── ImportItemProcessor.java
└── ImportItemWriter.java
```

1. Configure the job repository and input parameters.
2. Build a reader for the real source.
3. Put validation/transformation in the processor.
4. Build an idempotent writer where possible.
5. Choose chunk size from transaction cost and memory behavior.
6. Define which failures retry, skip, or fail the job.
7. Produce counts and error records required by operators.

Checkpoint: run a tiny valid input and confirm read/write counts, then rerun with one invalid record and confirm the chosen fail/skip behavior.

## 4. Attach required capabilities

- [Data storage](../capabilities/data-storage.md)
- [External API](../capabilities/external-api.md)—respect provider limits; avoid unbounded per-record calls
- [File storage](../capabilities/file-storage.md)
- [Messaging](../capabilities/messaging.md) when jobs are submitted asynchronously

## 5. Verify

Test empty input, valid input, malformed record, partial failure, retry/skip limits, restart from a failed step, duplicate job parameters, and realistic data volume.

```bash
./mvnw clean verify
```

## 6. Finish

Document job parameters, launch command, expected duration, output, recovery, and monitoring. Then complete the [production checklist](../docs/production-checklist.md).

Done means the job is restartable, bounded, observable, and produces an auditable result without silently losing records.
