# Process: Build an event-driven service

[← Choose another type](../README.md) · [Messaging](../capabilities/messaging.md) · [Testing](../docs/testing-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this when a broker message/event starts the main work or the service publishes domain events.

## Step 1 · Define one event contract

> 📍 Edit `<project-root>/PROJECT.md`, section **5. Feature sheet**; create/edit the producer-owned event contract file under `<project-root>/docs/events/`.

Record event name/version, producer, consumers, key, payload, ordering, duplicate handling, acknowledgement, retry, dead-letter, and compatibility.

```json
{
  "eventId": "uuid",
  "eventType": "order.created.v1",
  "occurredAt": "2030-01-01T10:00:00Z",
  "orderId": 42
}
```

Before continuing, check: Producer and consumer agree which fields are required and how old/new versions behave.

Continue to Step 2.

## Step 2 · Generate and connect the broker

> 📍 Open [Spring Initializr](https://start.spring.io/) in the browser. Edit `src/main/resources/application-local.yml`. Run the application from a terminal in `<project-root>/` and run the broker in its own terminal or container.

Select Actuator plus Spring for Apache Kafka or Spring for RabbitMQ; follow [messaging setup](../capabilities/messaging.md). Supply broker location through configuration.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Before continuing, check: Application starts and broker connection/health succeeds before listener code is added.

Continue to Step 3.

## Step 3 · Create event, listener, and service

> 📍 Under `src/main/java/com/company/project/`, create the `order/` folder and these files. Replace `com/company/project` with the package selected in Initializr.

```text
src/main/java/com/company/project/order/
├── OrderCreatedEvent.java
├── OrderEventListener.java
├── OrderService.java
├── ProcessedEvent.java
└── MessagingConfiguration.java
```

Example Kafka listener (use the equivalent Rabbit listener when RabbitMQ was selected):

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

Before continuing, check: Compile; publish one local event; listener deserializes it and calls the service once.

Continue to Step 4.

## Step 4 · Make processing idempotent and consistent

> 📍 Edit `src/main/java/com/company/project/order/OrderService.java`. Create `ProcessedEvent.java` and its repository in the same `order/` package. Create `src/main/java/com/company/project/messaging/OutboxEvent.java` and its publisher only when publication consistency is required.

Check/record `eventId` in the same transaction as the business change. Acknowledge only after the intended durable point. Use an outbox when database commit and publication must be consistent.

```java
@Transactional
public void processOrderCreated(OrderCreatedEvent event) {
    if (processedEvents.existsById(event.eventId())) return;
    billing.createFor(event.orderId());
    processedEvents.save(new ProcessedEvent(event.eventId()));
}
```

Before continuing, check: Send the same `eventId` twice; exactly one business effect remains.

Continue to Step 5.

## Step 5 · Configure retry, dead letter, and limits

> 📍 Edit `src/main/java/com/company/project/order/MessagingConfiguration.java` and `src/main/resources/application.yml`. Create `src/main/java/com/company/project/order/DeadLetterHandler.java` or the selected broker recovery handler.

Bound message size, concurrency, processing time, and retries. Retry transient failures with backoff; route permanent/exhausted failures to a dead-letter/recovery process. Never retry invalid schema forever.

Before continuing, check: A transient forced failure retries then succeeds; a permanent failure reaches recovery with event ID/error context.

Continue to Step 6.

## Step 6 · Test and deliver

> 📍 Create tests under `src/test/java/com/company/project/order/`. Edit `src/main/resources/application.yml`, the CI/deployment files in `<project-root>/`, and the contract under `<project-root>/docs/events/`. Run commands in `<project-root>/`.

Test valid/invalid event, duplicate, ordering assumption, retry, dead letter, broker outage, consumer restart, and scale-out using the [testing guide](../docs/testing-guide.md).

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

Before continuing, check: Clean build passes; duplicates are safe; failures are visible/recoverable; event contract is published.

Release, or return to Step 1 for another event flow.
