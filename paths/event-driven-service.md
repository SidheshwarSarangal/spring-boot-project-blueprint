# Path: Event-driven service

[← Choose another type](../README.md) · [Messaging](../capabilities/messaging.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once before Step 1.

Choose this when an event/message starts the main business action or the service primarily publishes events.

## 1. Define the event contract

```text
Event name and version:
Producer and consumers:
Payload and key:
Delivery expectation:
Ordering requirement:
Duplicate handling:
Retry/dead-letter rule:
```

Record the behavior in the [project workbook](../docs/project-workbook.md).

## 2. Generate and connect the broker

Select Actuator and the required broker integration, commonly Spring for Apache Kafka or Spring for RabbitMQ. Then follow [Messaging](../capabilities/messaging.md) for broker configuration and safety rules.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Continue only after the untouched application and required local broker start successfully.

## 3. Build one event flow

```text
message → listener → deserialize/validate → service
→ database/external adapter → acknowledge or retry
```

```text
src/main/java/com/company/project/order/
├── OrderCreatedEvent.java
├── OrderEventListener.java
├── OrderService.java
├── ProcessedEvent.java      optional idempotency record
└── MessagingConfiguration.java
```

1. Keep transport code in the listener/adapter.
2. Convert provider payloads to application-owned types.
3. Make processing idempotent using an event/operation ID.
4. Define when acknowledgement occurs.
5. Bound concurrency and processing time.
6. Publish events only after the related business state is safely recorded; use an outbox pattern when database/event consistency is required.

Checkpoint: publish one local test event, confirm one service execution and acknowledgement, then send the same event ID twice and confirm duplicate safety.

## 4. Attach required capabilities

- [Data storage](../capabilities/data-storage.md)
- [Security](../capabilities/security.md) for administrative HTTP endpoints
- [External API](../capabilities/external-api.md)
- [File storage](../capabilities/file-storage.md)

## 5. Verify

Test valid events, invalid schema, duplicate delivery, ordering assumptions, transient retry, dead-letter routing, broker outage, and restart behavior. Use integration tests with the real broker type when the flow is critical.

```bash
./mvnw clean verify
```

## 6. Finish

Document event ownership and compatibility, then complete the [production checklist](../docs/production-checklist.md).

Done means events are versioned, consumers tolerate duplicates, failures are recoverable/visible, and publishing cannot silently diverge from stored state.
