# Process: Build a real-time application

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this for chat, live tracking, dashboards, or notifications through SSE or WebSocket.

## Step 1 · Choose connection and message behavior

**What:** Define transport, connection lifecycle, messages, and missed-update behavior.

**Where:** One feature sheet in `PROJECT.md`.

**Do:** Choose SSE for server → client only, WebSocket for two-way messaging, or REST/polling when real-time is unnecessary. Record auth, subscribe/publish, ordering, reconnect, replay, size/rate limits, and disconnect cleanup.

**Verify:** Client behavior is defined for connect, normal message, disconnect, reconnect, and missed message.

**Next:** Step 2.

## Step 2 · Generate and run foundation

**What:** Start the selected real-time transport.

**Where:** Spring Initializr and project root.

**Do:** Select Spring Web for MVC/SSE or WebSocket for bidirectional messaging, plus Actuator; add Security for private connections.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

**Verify:** Application starts before connection code exists.

**Next:** Step 3.

## Step 3 · Implement one connection and message

**What:** Connect one client and deliver one typed update.

**Where:**

```text
src/main/java/com/company/project/live/
├── LiveMessage.java
├── LiveController.java
├── LiveUpdateService.java
└── WebSocketConfiguration.java  only for WebSocket
```

**Do:** Minimal MVC SSE example:

```java
public record LiveMessage(String type, String value, Instant occurredAt) {}

@RestController
@RequestMapping("/api/live")
class LiveController {
    private final LiveUpdateService updates;

    LiveController(LiveUpdateService updates) {
        this.updates = updates;
    }

    @GetMapping(path = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter events() {
        return updates.subscribe();
    }
}
```

The service owns emitter registration, send, completion, timeout, and removal. For WebSocket, define equivalent message DTOs and handlers/channel mappings.

**Verify:** One client connects, receives one message, disconnects, and is removed from server resources.

**Next:** Step 4.

## Step 4 · Add authentication, limits, and scale behavior

**What:** Protect and bound long-lived connections.

**Where:** Security config, update service, WebSocket/SSE configuration, selected broker/data package.

**Do:** Authenticate connection/subscription; authorize topics per user; bound message/rate/buffer/connection count; clean slow clients; define replay or REST recovery; add [messaging](../capabilities/messaging.md) for multi-instance fan-out and [data storage](../capabilities/data-storage.md) for durable history.

**Verify:** Forbidden subscription fails; oversized/fast/slow client is bounded; multi-instance behavior matches requirement.

**Next:** Step 5.

## Step 5 · Test and deliver

**What:** Prove lifecycle, security, capacity controls, and deployment.

**Where:** Connection/integration tests, configuration, CI/deployment, client connection guide.

**Do:** Test connect/auth, forbidden topic, publish/receive, malformed/oversized message, reconnect, slow client, cleanup, restart, and multi-instance fan-out using the [testing guide](../docs/testing-guide.md).

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

**Verify:** Clean build passes; connection lifecycle is bounded/observable; client recovery works in a clean deployment.

**Next:** Release, or return to Step 1 for another live flow.
