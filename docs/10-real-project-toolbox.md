# 10 · Real-project toolbox

[← Project workflow](00-project-workflow.md) · [Repository home](../README.md) · [Capability gate](00-project-workflow.md#gate-8--add-optional-capabilities-only-when-required)

| Before you act | Details |
|---|---|
| What | Select one optional capability required by a written project need. |
| Where | A dedicated adapter/configuration package connected through the service layer. |
| Input | Requirement, expected failures, data ownership, and operational limits. |
| Finish when | The capability is isolated, configured externally, and tested for success and failure. |

> **Terms:** An **adapter** isolates provider-specific code behind an application-owned interface. A **queue** stores work for background processing. A **cache** is a temporary copy used to speed reads. **Idempotent** work can be repeated without producing an extra effect. A **timeout** stops waiting for an external dependency after a fixed period.

Add capabilities because a requirement demands them—not because a diagram says mature systems have them.

## Select a capability from the requirement

```mermaid
flowchart TD
    Need["New requirement"] --> Type{"What kind?"}
    Type -->|"Store relational data"| SQL["JPA/JDBC + migrations"]
    Type -->|"Call another API"| HTTP["RestClient + timeout"]
    Type -->|"Slow background work"| Async["Queue / job worker"]
    Type -->|"Repeat on schedule"| Schedule["Scheduler + distributed lock"]
    Type -->|"Repeated expensive reads"| Cache["Cache after measuring"]
    Type -->|"Upload/download files"| Object["Object storage + metadata"]
    Type -->|"Notify users"| Notify["Email/SMS adapter"]
    Type -->|"Search large text"| Search["Search engine when SQL is insufficient"]
```

## Use the capability matrix

| Need | Common Spring choice | Add when | Main risk |
|---|---|---|---|
| REST API | Spring MVC | Serving HTTP/JSON | Inconsistent contracts |
| SQL persistence | Data JPA or JDBC | Durable relational data | N+1 and uncontrolled schema |
| Migrations | Flyway/Liquibase | Shared or production DB | Unsafe migration |
| Authentication | Spring Security | Any private/user data | Broken access control |
| API documentation | OpenAPI or Spring REST Docs | Other people call the API | Docs drift |
| External HTTP | `RestClient` | Calling another service | Missing timeouts/retries |
| Cache | Spring Cache + provider | Measured repeated cost | Stale data |
| Background work | Queue/broker + worker | Slow/retriable work | Duplicate delivery |
| Scheduling | `@Scheduled` | Time-based work | Multiple-instance duplication |
| Files | Object storage SDK | User-generated binaries | Unsafe files/access |
| Email/SMS | Provider adapter | Notifications | Coupling and retries |
| Observability | Actuator + Micrometer | Every deployed service | High-cardinality/sensitive labels |
| Resilience | Timeouts, retry, circuit breaker | Unreliable remote dependency | Retry storms |
| Containers | OCI image/buildpack | Reproducible deployment | Oversized/outdated image |

## Connect an external HTTP API

```mermaid
sequenceDiagram
    participant S as Application service
    participant A as Provider adapter
    participant C as RestClient
    participant X as External API
    S->>A: sendNotification(command)
    A->>C: Build authenticated request
    C->>X: HTTP with connect/read timeout
    alt Success
        X-->>C: 2xx response
        C-->>S: Result
    else Retryable failure
        X-->>C: timeout / 5xx
        C-->>S: bounded retry or failure
    end
```

Set timeouts. Retry only operations safe to repeat, use backoff, cap attempts, and observe failures.

## Move slow or retryable work to a background job

```mermaid
flowchart LR
    API["API request"] --> DB[("Save business state")]
    DB --> Event["Outbox/event"]
    Event --> Queue[("Queue")]
    Queue --> Worker["Worker"]
    Worker --> Provider["External provider"]
    Worker --> Result[("Job result")]
```

Assume messages may be delivered more than once. Make handlers idempotent and preserve an audit trail.

## Add a cache only after measuring

```mermaid
flowchart TD
    Read["Read request"] --> Hit{"Cache hit?"}
    Hit -- Yes --> Value["Return cached value"]
    Hit -- No --> DB[("Read database")]
    DB --> Store["Store with TTL"]
    Store --> Value
    Write["Write request"] --> Update[("Database update")]
    Update --> Invalidate["Invalidate / refresh cache"]
```

Caching creates a second copy of data. Define TTL, invalidation, ownership, and failure behavior before adding it.

## Store uploaded files outside the application filesystem

```mermaid
flowchart LR
    Client --> API["Authorized upload request"]
    API --> Validate["Type + size + malware policy"]
    Validate --> Storage[("Object storage")]
    Storage --> Key["Server-owned object key"]
    Key --> DB[("Metadata + owner")]
    Client --> Download["Authorized download / signed URL"]
```

Do not treat the original filename as a trusted filesystem path.

**Next:** Return to [Workflow Gate 8](00-project-workflow.md#gate-8--add-optional-capabilities-only-when-required) and run its verification.
