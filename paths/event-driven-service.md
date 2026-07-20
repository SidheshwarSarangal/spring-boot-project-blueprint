# Process: Build an event-driven service

[← Choose another type](../README.md) · [Messaging](../capabilities/messaging.md) · [Testing](../docs/testing-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this when a broker message/event starts the main work or the service publishes domain events.

## Repository action map

| Step | Exact location | Add or run there |
|---|---|---|
| 1 | Create `<project-root>/PROJECT.md` and event contract document | Versioned payload/delivery rules |
| 2 | Browser: Initializr; Terminal: project root + local broker environment | Generate/build/start/connect broker |
| 3 | Create `src/main/java/com/company/project/order/` | Event/listener/service/configuration files |
| 4 | Edit `order/OrderService.java`; create processed-event/inbox/outbox files | Idempotency/transaction code |
| 5 | Edit broker listener-container/recovery configuration | Retry/dead-letter/limits |
| 6 | Create matching `src/test/java/.../order/`; edit config/CI/event docs | Broker tests and delivery |

## Step 1 · Define one event contract

**What:** Produce a versioned event and delivery/failure agreement.

**Where:** `PROJECT.md` and the event documentation owned by the producer.

**Do:** Record event name/version, producer, consumers, key, payload, ordering, duplicate handling, acknowledgement, retry, dead-letter, and compatibility.

```json
{
  "eventId": "uuid",
  "eventType": "order.created.v1",
  "occurredAt": "2030-01-01T10:00:00Z",
  "orderId": 42
}
```

**Verify:** Producer and consumer agree which fields are required and how old/new versions behave.

**Next:** Step 2.

## Step 2 · Generate and connect the broker

**What:** Start the application and same broker type used by the target environment.

**Where:** Spring Initializr, `application.yml`, local broker environment.

**Do:** Select Actuator plus Spring for Apache Kafka or Spring for RabbitMQ; follow [messaging setup](../capabilities/messaging.md). Supply broker location through configuration.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

**Verify:** Application starts and broker connection/health succeeds before listener code is added.

**Next:** Step 3.

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

**Do:** Example Kafka listener (use the equivalent Rabbit listener when RabbitMQ was selected):

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

**Verify:** Compile; publish one local event; listener deserializes it and calls the service once.

**Next:** Step 4.

## Step 4 · Make processing idempotent and consistent

**What:** Prevent duplicate effects and database/message divergence.

**Where:** Service transaction, processed-event/inbox record, and publisher/outbox where applicable.

**Do:** Check/record `eventId` in the same transaction as the business change. Acknowledge only after the intended durable point. Use an outbox when database commit and publication must be consistent.

```java
@Transactional
public void processOrderCreated(OrderCreatedEvent event) {
    if (processedEvents.existsById(event.eventId())) return;
    billing.createFor(event.orderId());
    processedEvents.save(new ProcessedEvent(event.eventId()));
}
```

**Verify:** Send the same `eventId` twice; exactly one business effect remains.

**Next:** Step 5.

## Step 5 · Configure retry, dead letter, and limits

**What:** Make transient and permanent failure behavior explicit.

**Where:** Listener container/broker configuration and recovery handler.

**Do:** Bound message size, concurrency, processing time, and retries. Retry transient failures with backoff; route permanent/exhausted failures to a dead-letter/recovery process. Never retry invalid schema forever.

**Verify:** A transient forced failure retries then succeeds; a permanent failure reaches recovery with event ID/error context.

**Next:** Step 6.

## Step 6 · Test and deliver

**What:** Prove broker semantics and deploy safely.

**Where:** `src/test/java`, broker integration tests, configuration, CI/deployment, event docs.

**Do:** Test valid/invalid event, duplicate, ordering assumption, retry, dead letter, broker outage, consumer restart, and scale-out using the [testing guide](../docs/testing-guide.md).

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

**Verify:** Clean build passes; duplicates are safe; failures are visible/recoverable; event contract is published.

**Next:** Release, or return to Step 1 for another event flow.
