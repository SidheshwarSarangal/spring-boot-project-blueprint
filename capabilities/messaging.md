# Capability: Messaging

[← Application selector](../README.md) · [Event-driven path](../paths/event-driven-service.md)

Add Kafka, RabbitMQ, or another broker when work/events must be asynchronous, durable, buffered, or shared between services.

## 1. Define delivery behavior

```text
Message/event name and version:
Producer and consumer:
Key and ordering requirement:
At-least-once duplicate handling:
Acknowledgement point:
Retry and dead-letter rule:
Retention/replay requirement:
```

Assume duplicates can occur unless the complete system proves otherwise.

## 2. Choose the broker from requirements

- Kafka fits retained ordered streams, replay, and high-throughput event logs.
- RabbitMQ fits routed work queues and acknowledgement-based task delivery.
- Use the broker already operated by the organization when it satisfies the requirement.

Select Spring for Apache Kafka or Spring for RabbitMQ in Initializr. Supply broker addresses and credentials through environment-backed configuration. For local work, run the same broker type used in production and prove connectivity before writing the listener.

## 3. Implement

```text
producer/listener adapter ↔ broker ↔ listener/producer adapter
                              ↓
                        application service
```

1. Use versioned message DTOs.
2. Keep broker-specific types out of business services.
3. Include an event/operation ID and make consumers idempotent.
4. Bound consumer concurrency, queue size, message size, and processing time.
5. Retry transient failures with a limit/backoff.
6. Route permanent failures to a dead-letter/recovery process.
7. Use an outbox pattern when database state and message publication must stay consistent.

Typical files:

```text
src/main/java/com/company/project/messaging/
├── OrderCreatedEvent.java
├── OrderEventPublisher.java
├── OrderEventListener.java
├── MessagingConfiguration.java
└── DeadLetterHandler.java
```

Checkpoint: publish and consume one message locally, then repeat the same event ID and force one permanent failure before increasing concurrency.

## 4. Verify

Test publish/consume, invalid message, duplicate delivery, ordering assumption, retry, dead-letter routing, broker outage, consumer restart, and scale-out. Use the real broker type in critical integration tests.

Completion: duplicates are safe, failure is visible/recoverable, concurrency is bounded, and required messages survive restart/outage.
