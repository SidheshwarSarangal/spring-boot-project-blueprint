# Process: Build a real-time application

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this for chat, live tracking, dashboards, or notifications through SSE or WebSocket.

## Step 1 · Choose connection and message behavior

**What:** Define transport, connection lifecycle, messages, and missed-update behavior.

**Where:** Create or edit `<project-root>/PROJECT.md`, section **5. Feature sheet**.

**Do now:** Choose SSE for server → client only, WebSocket for two-way messaging, or REST/polling when real-time is unnecessary. Record auth, subscribe/publish, ordering, reconnect, replay, size/rate limits, and disconnect cleanup.

**Finish this step when:** Client behavior is defined for connect, normal message, disconnect, reconnect, and missed message.

**Go next:** Step 2.

## Step 2 · Generate and run foundation

**What:** Start the selected real-time transport.

**Where:** Browser at `https://start.spring.io`; terminal at `<project-root>` containing `pom.xml`.

**Do now:** Select Spring Web for MVC/SSE or WebSocket for bidirectional messaging, plus Actuator; add Security for private connections.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

**Finish this step when:** Application starts before connection code exists.

**Go next:** Step 3.

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

**Do now:** Minimal MVC SSE example:

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

**Finish this step when:** One client connects, receives one message, disconnects, and is removed from server resources.

**Go next:** Step 4.

## Step 4 · Add authentication, limits, and scale behavior

**What:** Protect and bound long-lived connections.

**Where:** Edit `live/LiveUpdateService.java`, `live/WebSocketConfiguration.java` when used, `security/SecurityConfiguration.java`, and only the linked `messaging/` or persistence files required.

**Do now:** Authenticate connection/subscription; authorize topics per user; bound message/rate/buffer/connection count; clean slow clients; define replay or REST recovery; add [messaging](../capabilities/messaging.md) for multi-instance fan-out and [data storage](../capabilities/data-storage.md) for durable history.

**Finish this step when:** Forbidden subscription fails; oversized/fast/slow client is bounded; multi-instance behavior matches requirement.

**Go next:** Step 5.

## Step 5 · Test and deliver

**What:** Prove lifecycle, security, capacity controls, and deployment.

**Where:** Create tests under `src/test/java/com/company/project/live/`; edit `application.yml`, root CI/deployment files, and `<project-root>/docs/live-client.md`; run commands at `<project-root>`.

**Do now:** Test connect/auth, forbidden topic, publish/receive, malformed/oversized message, reconnect, slow client, cleanup, restart, and multi-instance fan-out using the [testing guide](../docs/testing-guide.md).

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

**Finish this step when:** Clean build passes; connection lifecycle is bounded/observable; client recovery works in a clean deployment.

**Go next:** Release, or return to Step 1 for another live flow.
