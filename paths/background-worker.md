# Process: Build a background or scheduled worker

[← Choose another type](../README.md) · [Working starter](../starters/background-worker/README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this when work runs on a timer or outside the original HTTP request.

## Step 1 · Define one job

> 📍 Create or edit `<project-root>/PROJECT.md`, section **5. Feature sheet**.

Record trigger, input/job ID, output/state change, maximum duration, duplicate rule, retry rule, and recovery owner.

```text
Trigger: every 15 minutes
Action: expire unpaid orders older than 24 hours
Batch limit: 100 records/run
Duplicate behavior: safe; already-expired orders are skipped
Failure: record failure and retry on next schedule
```

Before continuing, check: Success and safe rerun can be tested without waiting for production time.

Continue to Step 2.

## Step 2 · Choose mechanism and create foundation

> 📍 Open [Spring Initializr](https://start.spring.io/) in the browser. After extracting the project, open a terminal in `<project-root>/`, the folder containing `pom.xml` and `mvnw`.

Use `@Scheduled` for simple timed work, `@Async` only for non-durable in-process work, [messaging](../capabilities/messaging.md) when required work must survive restart, or the [batch process](batch-application.md) for large restartable datasets. Select Actuator plus required dependencies.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Before continuing, check: Untouched application starts and selected local dependencies are reachable.

Continue to Step 3.

## Step 3 · Create trigger, service, and configuration

> 📍 Under `src/main/java/com/company/project/`, create the `cleanup/` folder and these files. Replace `com/company/project` with the package selected in Initializr.

```text
src/main/java/com/company/project/cleanup/
├── CleanupJob.java
├── CleanupService.java
├── CleanupProperties.java
└── JobExecutionRecord.java   optional durable state
```

Enable scheduling in a configuration class and create a thin trigger:

```java
@Configuration
@EnableScheduling
class SchedulingConfiguration {}

@Component
class CleanupJob {
    private final CleanupService service;

    CleanupJob(CleanupService service) {
        this.service = service;
    }

    @Scheduled(cron = "${jobs.cleanup.cron}")
    void run() {
        service.expireOldOrders();
    }
}
```

```yaml
jobs:
  cleanup:
    cron: "0 */15 * * * *"
```

Before continuing, check: `./mvnw compile` passes and application starts with the property present.

Continue to Step 4.

## Step 4 · Implement bounded, repeatable work

> 📍 Edit `src/main/java/com/company/project/cleanup/CleanupService.java`. Create the feature entity and repository in the same `cleanup/` package. Create `src/main/java/com/company/project/cleanup/JobExecutionRecord.java` only when durable execution history is required.

Query a bounded work set, make each state transition idempotent, transact database changes, record outcome, and set concurrency limits. For multiple instances, add a distributed scheduling lock or move required work to a broker.

```java
@Service
class CleanupService {
    private final OrderRepository orders;

    CleanupService(OrderRepository orders) {
        this.orders = orders;
    }

    @Transactional
    public int expireOldOrders() {
        List<Order> batch = orders.findNextUnpaidBatch(
            Instant.now().minus(24, ChronoUnit.HOURS), PageRequest.of(0, 100)
        );
        batch.forEach(Order::expireIfUnpaid);
        return batch.size();
    }
}
```

Inject a `Clock` instead of calling current time directly when the time rule requires deterministic testing.

Before continuing, check: Call the service in a test; run the trigger once locally; run it again and confirm state is not corrupted or duplicated.

Continue to Step 5.

## Step 5 · Add recovery, observability, and required capabilities

> 📍 Edit `src/main/java/com/company/project/cleanup/CleanupJob.java`, `CleanupService.java`, and `src/main/resources/application.yml`. Create only the capability folder linked by this step.

Add [data storage](../capabilities/data-storage.md), [external API](../capabilities/external-api.md), [messaging](../capabilities/messaging.md), or [file storage](../capabilities/file-storage.md) only when required. Record start/end, count, duration, outcome, and safe correlation ID. Retry only transient operations with a limit/backoff.

Before continuing, check: Forced timeout/permanent failure becomes a visible recorded/alertable outcome and cannot loop indefinitely.

Continue to Step 6.

## Step 6 · Test, configure, and deliver

> 📍 Create tests under `src/test/java/com/company/project/cleanup/`; edit `src/main/resources/application.yml`, root CI/deployment files, and `<project-root>/README.md`; run commands at `<project-root>`.

Test service rules, thin trigger delegation, duplicate execution, retry exhaustion, restart/recovery, and concurrency using the [testing guide](../docs/testing-guide.md).

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

Before continuing, check: Clean build passes; required work is not silently lost; operations expose job age/success/failure.

Release, or return to Step 1 for another job.
