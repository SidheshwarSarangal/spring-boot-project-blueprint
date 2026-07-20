# Capability process: Add messaging

[← Application selector](../README.md) · [Event process](../paths/event-driven-service.md) · [Testing](../docs/testing-guide.md)

Insert this process when work/events must be asynchronous, durable, buffered, replayable, or shared between services.

## Step 1 · Define message and delivery behavior

**What:** Produce the message/event contract and recovery rules.

**Where:** Add an `Event contract` section under the current feature in `<project-root>/PROJECT.md`.

**Do now:** Record name/version, producer, consumer, key, payload, ordering, expected delivery, acknowledgement, duplicate, retry, dead-letter, retention, and replay.

**Finish this step when:** Duplicate and failure behavior are explicit; “exactly once” is not assumed casually.

**Go next:** Step 2.

## Step 2 · Choose broker, dependency, and local connection

**What:** Run the same broker type used by the target environment.

**Where:** Edit `<project-root>/pom.xml` and `src/main/resources/application-local.yml`; start the local broker from its project/container directory and keep credentials outside Git.

**Do now:** Choose Kafka for retained ordered streams/replay/high throughput; RabbitMQ for routed work queues/acknowledgement delivery; prefer the organization-operated broker when suitable. Add Spring for Apache Kafka or Spring for RabbitMQ.

Kafka example:

```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    consumer:
      group-id: ${KAFKA_GROUP_ID:orders-local}
```

**Finish this step when:** Application connects and broker health/metadata succeeds before listener/publisher code.

**Go next:** Step 3.

## Step 3 · Create typed publisher/listener adapters

**What:** Send/receive one typed event without broker types in business services.

**Where:** Create these paths; replace `com/company/project` with the package selected in Initializr.

```text
src/main/java/com/company/project/messaging/
├── OrderCreatedEvent.java
├── OrderEventPublisher.java
├── OrderEventListener.java
└── MessagingConfiguration.java
```

**Do now:** Kafka example:

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

**Finish this step when:** Publish and consume one local message; payload converts to the expected application event.

**Go next:** Step 4.

## Step 4 · Add idempotency, acknowledgement, retry, and dead letter

**What:** Make duplicate and failure handling safe and bounded.

**Where:** Edit `src/main/java/com/company/project/messaging/OrderEventListener.java` and `MessagingConfiguration.java`; put business work in the feature service; add inbox/outbox entities and repositories under `messaging/` only when required.

**Do now:** Include operation/event ID; make consumer idempotent; acknowledge after intended durable work; retry transient failures with limit/backoff; route invalid/permanent/exhausted messages to recovery; use outbox when DB state and publication must be consistent. Bound size, concurrency, and processing time.

**Finish this step when:** Duplicate produces one effect; transient error retries; permanent error reaches recovery; broker restart does not silently lose required work.

**Go next:** Step 5.

## Step 5 · Integration-test the real broker type

**What:** Prove protocol/configuration assumptions and scale behavior.

**Where:** Create `src/test/java/com/company/project/messaging/MessagingIntegrationTest.java`; configure metrics/logging in `src/main/resources/application.yml`; run tests in `<project-root>/` and the same command in CI.

**Do now:** Test publish/consume, invalid message, duplicate, ordering, retry, dead letter, broker outage, consumer restart, and multiple consumers. Observe lag/queue depth, outcome, retry, and dead-letter counts.

```bash
./mvnw clean verify
```

**Finish this step when:** Critical tests use the selected broker type; duplicates/failures are visible and recoverable.

**Go next:** Return to the application path’s next step.
