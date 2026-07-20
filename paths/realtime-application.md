# Process: Build a real-time application

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this for chat, live tracking, dashboards, or notifications through SSE or WebSocket.

## Step 1 · Choose connection and message behavior

Create or edit `<project-root>/PROJECT.md`, section **5. Feature sheet**.

Choose SSE for server → client only, WebSocket for two-way messaging, or REST/polling when real-time is unnecessary. Record auth, subscribe/publish, ordering, reconnect, replay, size/rate limits, and disconnect cleanup.

Before continuing, check: Client behavior is defined for connect, normal message, disconnect, reconnect, and missed message.

Continue to Step 2.

## Step 2 · Generate and run foundation

Open [Spring Initializr](https://start.spring.io/) in the browser. After extracting the project, open a terminal in `<project-root>/`, the folder containing `pom.xml` and `mvnw`.

Select Spring Web for MVC/SSE or WebSocket for bidirectional messaging, plus Actuator; add Security for private connections.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Before continuing, check: Application starts before connection code exists.

Continue to Step 3.

## Step 3 · Implement one connection and message

Under `src/main/java/com/company/project/`, create the `live/` folder and these files. Replace `com/company/project` with the package selected in Initializr.

```text
src/main/java/com/company/project/live/
├── LiveMessage.java
├── LiveController.java
├── LiveUpdateService.java
└── WebSocketConfiguration.java  only for WebSocket
```

Minimal MVC SSE example:

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

Before continuing, check: One client connects, receives one message, disconnects, and is removed from server resources.

Continue to Step 4.

## Step 4 · Add authentication, limits, and scale behavior

Edit `src/main/java/com/company/project/live/LiveUpdateService.java` and `WebSocketConfiguration.java` when WebSocket is used. Edit `src/main/java/com/company/project/security/SecurityConfiguration.java`, then add only the linked messaging or persistence files required.

Authenticate connection/subscription; authorize topics per user; bound message/rate/buffer/connection count; clean slow clients; define replay or REST recovery; add [messaging](../capabilities/messaging.md) for multi-instance fan-out and [data storage](../capabilities/data-storage.md) for durable history.

Before continuing, check: Forbidden subscription fails; oversized/fast/slow client is bounded; multi-instance behavior matches requirement.

Continue to Step 5.

## Step 5 · Test and deliver

Create tests under `src/test/java/com/company/project/live/`. Edit `src/main/resources/application.yml`, the CI/deployment files in `<project-root>/`, and `<project-root>/docs/live-client.md`. Run commands in `<project-root>/`.

Test connect/auth, forbidden topic, publish/receive, malformed/oversized message, reconnect, slow client, cleanup, restart, and multi-instance fan-out using the [testing guide](../docs/testing-guide.md).

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

Before continuing, check: Clean build passes; connection lifecycle is bounded/observable; client recovery works in a clean deployment.

Release, or return to Step 1 for another live flow.
