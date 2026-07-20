# Process: Build an event-driven service

[← Choose another type](../README.md) · [Messaging](../capabilities/messaging.md) · [Testing](../docs/testing-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this when a broker message/event starts the main work or the service publishes domain events.

## Step 1 · Define one event contract

**What:** Produce a versioned event and delivery/failure agreement.

**Where:** Edit `<project-root>/PROJECT.md`, section **5. Feature sheet**; create/edit the producer-owned event contract file under `<project-root>/docs/events/`.

**Do now:** Record event name/version, producer, consumers, key, payload, ordering, duplicate handling, acknowledgement, retry, dead-letter, and compatibility.

```json
{
  "eventId": "uuid",
  "eventType": "order.created.v1",
  "occurredAt": "2030-01-01T10:00:00Z",
  "orderId": 42
}
```

**Finish this step when:** Producer and consumer agree which fields are required and how old/new versions behave.

**Go next:** Step 2.

## Step 2 · Generate and connect the broker

**What:** Start the application and same broker type used by the target environment.

**Where:** Browser at `https://start.spring.io`; edit `src/main/resources/application-local.yml`; run the app at `<project-root>` and broker in its separate local terminal/container environment.

**Do now:** Select Actuator plus Spring for Apache Kafka or Spring for RabbitMQ; follow [messaging setup](../capabilities/messaging.md). Supply broker location through configuration.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

**Finish this step when:** Application starts and broker connection/health succeeds before listener code is added.

**Go next:** Step 3.

## Step 3 · Create event, listener, and service

**What:** Establish message adapter → service without broker types in business logic.

**Where:**

```text
src/main/java/com/company/project/order/
├── OrderCreatedEvent.java
├── OrderEventListener.java
├── OrderService.java
├── ProcessedEvent.java
└── MessagingConfiguration.java
```

**Do now:** Example Kafka listener (use the equivalent Rabbit listener when RabbitMQ was selected):

```java
public record OrderCreatedEvent(UUID eventId, Long orderId, Instant occurredAt) {}

@Component
class OrderEventListener {
    private final OrderService service;

    OrderEventListener(OrderService service) {
        this.service = service;
    }

    @KafkaListener(topics = "orders.created", groupId = "billing")
    void onOrderCreated(OrderCreatedEvent event) {
        service.processOrderCreated(event);
    }
}
```

**Finish this step when:** Compile; publish one local event; listener deserializes it and calls the service once.

**Go next:** Step 4.

## Step 4 · Make processing idempotent and consistent

**What:** Prevent duplicate effects and database/message divergence.

**Where:** Edit `src/main/java/com/company/project/order/OrderService.java`; create `order/ProcessedEvent.java` and repository; create `messaging/OutboxEvent.java` and publisher only when publication consistency is required.

**Do now:** Check/record `eventId` in the same transaction as the business change. Acknowledge only after the intended durable point. Use an outbox when database commit and publication must be consistent.

```java
@Transactional
public void processOrderCreated(OrderCreatedEvent event) {
    if (processedEvents.existsById(event.eventId())) return;
    billing.createFor(event.orderId());
    processedEvents.save(new ProcessedEvent(event.eventId()));
}
```

**Finish this step when:** Send the same `eventId` twice; exactly one business effect remains.

**Go next:** Step 5.

## Step 5 · Configure retry, dead letter, and limits

**What:** Make transient and permanent failure behavior explicit.

**Where:** Edit `order/MessagingConfiguration.java` and `src/main/resources/application.yml`; create `order/DeadLetterHandler.java` or the selected broker recovery handler.

**Do now:** Bound message size, concurrency, processing time, and retries. Retry transient failures with backoff; route permanent/exhausted failures to a dead-letter/recovery process. Never retry invalid schema forever.

**Finish this step when:** A transient forced failure retries then succeeds; a permanent failure reaches recovery with event ID/error context.

**Go next:** Step 6.

## Step 6 · Test and deliver

**What:** Prove broker semantics and deploy safely.

**Where:** Create tests under `src/test/java/com/company/project/order/`; edit broker config, root CI/deployment files, and `docs/events/`; run commands at `<project-root>`.

**Do now:** Test valid/invalid event, duplicate, ordering assumption, retry, dead letter, broker outage, consumer restart, and scale-out using the [testing guide](../docs/testing-guide.md).

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

**Finish this step when:** Clean build passes; duplicates are safe; failures are visible/recoverable; event contract is published.

**Go next:** Release, or return to Step 1 for another event flow.
