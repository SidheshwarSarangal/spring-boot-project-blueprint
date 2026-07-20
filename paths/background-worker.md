# Process: Build a background or scheduled worker

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this when work runs on a timer or outside the original HTTP request.

## Repository action map

| Step | Exact location | Add or run there |
|---|---|---|
| 1 | Create `<project-root>/PROJECT.md` | Job contract/retry/duplicate rules |
| 2 | Browser: Initializr; Terminal: generated `<project-root>` | Generate/build/start |
| 3 | Create `src/main/java/com/company/project/cleanup/`; edit `src/main/resources/application.yml` | Trigger/service/properties/schedule |
| 4 | Edit `cleanup/CleanupService.java`; create/edit feature repository/entity as required | Bounded idempotent job code |
| 5 | Edit selected capability/configuration files; add metrics/logs in job/service | Recovery/observability |
| 6 | Create `src/test/java/com/company/project/cleanup/`; edit config/CI/README | Job tests and delivery |

## Step 1 · Define one job

**What:** Produce the trigger, result, failure, and duplicate-execution contract.

**Where:** One job feature sheet in `PROJECT.md`.

**Do:** Record trigger, input/job ID, output/state change, maximum duration, duplicate rule, retry rule, and recovery owner.

```text
Trigger: every 15 minutes
Action: expire unpaid orders older than 24 hours
Batch limit: 100 records/run
Duplicate behavior: safe; already-expired orders are skipped
Failure: record failure and retry on next schedule
```

**Verify:** Success and safe rerun can be tested without waiting for production time.

**Next:** Step 2.

## Step 2 · Choose mechanism and create foundation

**What:** Select the smallest execution mechanism and run an empty application.

**Where:** Spring Initializr and project root.

**Do:** Use `@Scheduled` for simple timed work, `@Async` only for non-durable in-process work, [messaging](../capabilities/messaging.md) when required work must survive restart, or the [batch process](batch-application.md) for large restartable datasets. Select Actuator plus required dependencies.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

**Verify:** Untouched application starts and selected local dependencies are reachable.

**Next:** Step 3.

## Step 3 · Create trigger, service, and configuration

**What:** Separate timing from the complete business operation.

**Where:**

```text
src/main/java/com/company/project/cleanup/
├── CleanupJob.java
├── CleanupService.java
├── CleanupProperties.java
└── JobExecutionRecord.java   optional durable state
```

**Do:** Enable scheduling in a configuration class and create a thin trigger:

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

**Verify:** `./mvnw compile` passes and application starts with the property present.

**Next:** Step 4.

## Step 4 · Implement bounded, repeatable work

**What:** Complete the job safely inside the service.

**Where:** `CleanupService.java`, repository/adapter, and optional execution entity.

**Do:** Query a bounded work set, make each state transition idempotent, transact database changes, record outcome, and set concurrency limits. For multiple instances, add a distributed scheduling lock or move required work to a broker.

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

**Verify:** Call the service in a test; run the trigger once locally; run it again and confirm state is not corrupted or duplicated.

**Next:** Step 5.

## Step 5 · Add recovery, observability, and required capabilities

**What:** Make slow/failing jobs bounded and diagnosable.

**Where:** Job configuration, service, metrics/logging, and selected capability package.

**Do:** Add [data storage](../capabilities/data-storage.md), [external API](../capabilities/external-api.md), [messaging](../capabilities/messaging.md), or [file storage](../capabilities/file-storage.md) only when required. Record start/end, count, duration, outcome, and safe correlation ID. Retry only transient operations with a limit/backoff.

**Verify:** Forced timeout/permanent failure becomes a visible recorded/alertable outcome and cannot loop indefinitely.

**Next:** Step 6.

## Step 6 · Test, configure, and deliver

**What:** Prove job behavior across success, duplication, failure, and restart.

**Where:** `src/test/java`, application configuration, CI/deployment files, README.

**Do:** Test service rules, thin trigger delegation, duplicate execution, retry exhaustion, restart/recovery, and concurrency using the [testing guide](../docs/testing-guide.md).

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

**Verify:** Clean build passes; required work is not silently lost; operations expose job age/success/failure.

**Next:** Release, or return to Step 1 for another job.
