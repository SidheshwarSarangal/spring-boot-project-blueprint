# Path: Background or scheduled worker

[← Choose another type](../README.md) · [Troubleshooting](../docs/troubleshooting.md)

Choose this when work runs on a timer or outside the original HTTP request.

## 1. Define the job

```text
Trigger: schedule, internal event, or submitted task
Input: data/job identifier
Output: state change, generated result, or external action
Failure: retry, skip, alert, or manual recovery
Duplicate rule: what happens if it runs twice?
```

Record it in the [project workbook](../docs/project-workbook.md).

## 2. Choose the mechanism

| Need | Use |
|---|---|
| Small non-durable in-process task | `@Async` with a configured executor |
| Simple timed maintenance | `@Scheduled` |
| Work must survive restart or traffic spikes | [Messaging](../capabilities/messaging.md) |
| Large restartable dataset | [Batch application](batch-application.md) |

Generate a minimal Spring Boot project with Actuator and only the dependencies required by the trigger/data.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Continue only after the untouched application starts.

## 3. Implement one job

```text
trigger/listener → job service → repository/adapter → recorded outcome
```

1. Keep the trigger thin; pass an identifier to the service.
2. Make the operation idempotent when repetition is possible.
3. Configure a bounded executor or consumer concurrency.
4. Record job status and enough context to diagnose failure.
5. Apply limited retry only to transient failures.
6. Ensure multiple application instances cannot accidentally duplicate a scheduled job.

## 4. Attach required capabilities

- [Data storage](../capabilities/data-storage.md) for durable job state
- [External API](../capabilities/external-api.md)
- [Messaging](../capabilities/messaging.md)
- [File storage](../capabilities/file-storage.md)

## 5. Verify

Test success, duplicate execution, timeout, retry exhaustion, permanent failure, restart/recovery, and concurrency limits. Expose useful health/metrics for job age, success, and failure.

```bash
./mvnw clean verify
```

## 6. Finish

Repeat for the next job, then complete the [production checklist](../docs/production-checklist.md).

Done means work is bounded, observable, safely repeatable, and not silently lost when durability is required.
