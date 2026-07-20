# Process: Build a real-time application

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this for chat, live tracking, dashboards, or notifications through SSE or WebSocket.

## Repository action map

| Step | Exact location | Add or run there |
|---|---|---|
| 1 | Create `<project-root>/PROJECT.md` | Connection/message/reconnect contract |
| 2 | Browser: Initializr; Terminal: generated `<project-root>` | Generate/build/start transport |
| 3 | Create `src/main/java/com/company/project/live/` | Message/controller/service/WebSocket config |
| 4 | Edit `live/`, `security/`, and selected messaging/data packages | Auth, limits, fan-out, history |
| 5 | Create matching `src/test/java/.../live/`; edit client docs/config/CI | Lifecycle tests and delivery |

**Beginner actions by step:** 1 → [A workbook](../docs/beginner-execution-guide.md#action-a-create-the-working-repository-and-workbook); 2 → [B generate](../docs/beginner-execution-guide.md#action-b-generate-the-spring-project-in-the-browser), [D terminal](../docs/beginner-execution-guide.md#action-d-run-a-command-in-the-correct-terminal); 3–4 → [E create files](../docs/beginner-execution-guide.md#action-e-create-a-java-package-and-file), [F add code](../docs/beginner-execution-guide.md#action-f-put-a-provided-java-code-block-into-a-file), [J connect/call](../docs/beginner-execution-guide.md#action-j-start-the-application-and-call-it); 5 → [K tests](../docs/beginner-execution-guide.md#action-k-create-and-run-a-test), [M checkpoint](../docs/beginner-execution-guide.md#action-m-save-a-clean-checkpoint-with-git).

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
