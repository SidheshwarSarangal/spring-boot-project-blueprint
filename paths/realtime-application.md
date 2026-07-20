# Path: Real-time application

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once before Step 1.

Choose this for chat, live tracking, dashboards, notifications, or streaming updates through WebSocket or Server-Sent Events (SSE).

## 1. Choose the connection model

| Need | Use |
|---|---|
| Server pushes updates; client only receives | SSE |
| Both client and server send messages | WebSocket |
| Ordinary request/response | Use the [REST API path](rest-api.md) instead |

Define connection lifetime, message types, authentication, ordering, reconnect, and missed-message behavior in the [project workbook](../docs/project-workbook.md).

## 2. Generate the project

Select Spring Web for MVC/SSE or WebSocket for bidirectional messaging, plus Actuator. Add Spring Security when connections or channels are private.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Continue only after the untouched application starts.

## 3. Build one live flow

```text
connect/authenticate → subscribe → service receives state/event
→ publish update → client handles/reconnects
```

```text
src/main/java/com/company/project/live/
├── LiveMessage.java
├── LiveController.java       SSE/WebSocket entry point
├── LiveUpdateService.java
├── WebSocketConfiguration.java  when WebSocket is selected
└── LiveSecurityConfiguration.java
```

1. Define small versioned message DTOs.
2. Authenticate at connection/subscription boundaries.
3. Authorize channels/topics per user or role.
4. Keep business state in services, not connection handlers.
5. Bound message size, rate, buffers, and connection count.
6. Decide whether missed messages are discarded, replayed, or read through a REST endpoint.
7. Clean up disconnected/slow clients.

Checkpoint: connect one local client, receive one message, disconnect, and confirm server resources are released before adding fan-out or persistence.

## 4. Attach required capabilities

- [Security](../capabilities/security.md)
- [Messaging](../capabilities/messaging.md) for multi-instance fan-out/durable events
- [Data storage](../capabilities/data-storage.md) for durable state/history
- [REST API](rest-api.md) for initial state and recovery

## 5. Verify

Test connection, authentication, forbidden subscription, publish/receive, malformed/oversized messages, reconnect, slow client, disconnect cleanup, and multiple application instances where applicable.

```bash
./mvnw clean verify
```

## 6. Finish

Document client connection/reconnect behavior and capacity limits, then complete the [production checklist](../docs/production-checklist.md).

Done means connections are authenticated and bounded, missed-message behavior is explicit, and the system works correctly across restart and scale-out requirements.
