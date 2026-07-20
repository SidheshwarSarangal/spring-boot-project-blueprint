# Capability process: Add messaging

[← Application selector](../README.md) · [Event process](../paths/event-driven-service.md) · [Testing](../docs/testing-guide.md)

Insert this process when work/events must be asynchronous, durable, buffered, replayable, or shared between services.

## Repository action map

| Step | Exact location | Add or run there |
|---|---|---|
| 1 | Edit feature/event sheet in `<project-root>/PROJECT.md` | Payload/delivery/recovery contract |
| 2 | Edit `pom.xml`, `application-local.yml`; run broker separately and app terminal at project root | Broker dependency/config/connectivity |
| 3 | Create `src/main/java/com/company/project/messaging/` | Event/publisher/listener/config code |
| 4 | Edit consumer service; create inbox/outbox/recovery files and broker config | Idempotency/ack/retry/dead letter |
| 5 | Create matching broker tests under `src/test/java`; run terminal at project root | Real-broker integration proof |

## Step 1 · Define message and delivery behavior

**What:** Produce the message/event contract and recovery rules.

**Where:** Feature sheet/event documentation in `PROJECT.md`.

**Do:** Record name/version, producer, consumer, key, payload, ordering, expected delivery, acknowledgement, duplicate, retry, dead-letter, retention, and replay.

**Verify:** Duplicate and failure behavior are explicit; “exactly once” is not assumed casually.

**Next:** Step 2.

## Step 2 · Choose broker, dependency, and local connection

**What:** Run the same broker type used by the target environment.

**Where:** Spring Initializr/`pom.xml`, local broker, environment-backed `application-local.yml`.

**Do:** Choose Kafka for retained ordered streams/replay/high throughput; RabbitMQ for routed work queues/acknowledgement delivery; prefer the organization-operated broker when suitable. Add Spring for Apache Kafka or Spring for RabbitMQ.

Kafka example:

```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    consumer:
      group-id: ${KAFKA_GROUP_ID:orders-local}
```

**Verify:** Application connects and broker health/metadata succeeds before listener/publisher code.

**Next:** Step 3.

## Step 3 · Create typed publisher/listener adapters

**What:** Send/receive one typed event without broker types in business services.

**Where:**

```text
src/main/java/com/company/project/messaging/
├── OrderCreatedEvent.java
├── OrderEventPublisher.java
├── OrderEventListener.java
└── MessagingConfiguration.java
```

**Do:** Kafka example:

```java
public record OrderCreatedEvent(UUID eventId, Long orderId, Instant occurredAt) {}

@Component
class OrderEventPublisher {
    private final KafkaTemplate<String, OrderCreatedEvent> kafka;

    OrderEventPublisher(KafkaTemplate<String, OrderCreatedEvent> kafka) {
        this.kafka = kafka;
    }

    void publish(OrderCreatedEvent event) {
        kafka.send("orders.created", event.orderId().toString(), event);
    }
}
```

Use `@KafkaListener`/`@RabbitListener` only in adapter classes, then delegate to a service.

**Verify:** Publish and consume one local message; payload converts to the expected application event.

**Next:** Step 4.

## Step 4 · Add idempotency, acknowledgement, retry, and dead letter

**What:** Make duplicate and failure handling safe and bounded.

**Where:** Consumer service transaction, inbox/processed-event storage, listener container/recovery configuration, dead-letter handler.

**Do:** Include operation/event ID; make consumer idempotent; acknowledge after intended durable work; retry transient failures with limit/backoff; route invalid/permanent/exhausted messages to recovery; use outbox when DB state and publication must be consistent. Bound size, concurrency, and processing time.

**Verify:** Duplicate produces one effect; transient error retries; permanent error reaches recovery; broker restart does not silently lose required work.

**Next:** Step 5.

## Step 5 · Integration-test the real broker type

**What:** Prove protocol/configuration assumptions and scale behavior.

**Where:** Broker integration tests/Testcontainers, metrics/logging, CI.

**Do:** Test publish/consume, invalid message, duplicate, ordering, retry, dead letter, broker outage, consumer restart, and multiple consumers. Observe lag/queue depth, outcome, retry, and dead-letter counts.

```bash
./mvnw clean verify
```

**Verify:** Critical tests use the selected broker type; duplicates/failures are visible and recoverable.

**Next:** Return to the application path’s next step.
