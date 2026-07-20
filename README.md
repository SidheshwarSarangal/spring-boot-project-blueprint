# What do you want to build with Spring Boot?

Choose the result your application must produce. Open only that path; it contains the complete order from project setup to delivery.

> **First Java or Spring Boot project?** Complete the [Java and Spring Boot foundation](docs/java-spring-foundation.md) once, then return here and select a path. Experienced readers can select immediately.

## Select one primary application type

| I need to build… | Choose this path |
|---|---|
| A JSON backend for a frontend, mobile app, or another service | [REST API](paths/rest-api.md) |
| A schema-driven API where clients choose fields | [GraphQL API](paths/graphql-api.md) |
| HTML pages and forms rendered by Spring | [Web application](paths/web-application.md) |
| Work that runs at a time or outside an HTTP request | [Background worker](paths/background-worker.md) |
| A service that consumes or publishes events | [Event-driven service](paths/event-driven-service.md) |
| A service mainly connecting to another API/provider | [Integration service](paths/integration-service.md) |
| A single entry point that routes and protects downstream services | [API gateway](paths/api-gateway.md) |
| Large, restartable data import/export or processing | [Batch application](paths/batch-application.md) |
| Live server-to-client updates, chat, tracking, or notifications | [Real-time application](paths/realtime-application.md) |
| A terminal command or one-off automation utility | [Command-line application](paths/command-line-application.md) |

> Most business domains—shopping, banking, hospital, booking, learning—use one or more of these technical shapes. Select by how the application runs and delivers results, not by its business name.

“Monolith,” “modular monolith,” “microservice,” and “serverless” describe system boundaries or deployment. They do not replace the choices above. Start with the selected application path; split deployment units only when team, scaling, ownership, or reliability requirements justify it.

## Not sure which one?

```mermaid
flowchart TD
    Start{What starts the main work?}
    Start -->|HTTP request expecting JSON| REST[REST API]
    Start -->|Schema query/mutation| GRAPH[GraphQL API]
    Start -->|Browser page or form| WEB[Web application]
    Start -->|Timer or internal trigger| BG[Background worker]
    Start -->|Message or domain event| EVENT[Event-driven service]
    Start -->|Large finite dataset/job| BATCH[Batch application]
    Start -->|Another provider is the main dependency| INT[Integration service]
    Start -->|Route requests to services| GATE[API gateway]
    Start -->|Long-lived live connection| RT[Real-time application]
    Start -->|Terminal command| CLI[Command-line application]
```

## Add capabilities only when the selected path asks for them

| The application also needs… | Capability module |
|---|---|
| SQL or MongoDB persistence | [Data storage](capabilities/data-storage.md) |
| Login, roles, permissions, or private records | [Security](capabilities/security.md) |
| Payments, email, maps, AI, or another HTTP API | [External API](capabilities/external-api.md) |
| Kafka, RabbitMQ, or durable asynchronous work | [Messaging](capabilities/messaging.md) |
| Uploads, downloads, images, or documents | [File storage](capabilities/file-storage.md) |
| Faster repeated reads after a measured bottleneck | [Caching](capabilities/caching.md) |

Do not read or add every capability. Complete the primary path, attach one required capability at its stated step, verify it, and continue.

## How every selected process works

Every numbered step in a path or capability uses the same five instructions:

| Instruction | Meaning |
|---|---|
| **What** | The result this step must produce |
| **Where** | Exact project directory or file to work in |
| **Do** | Actions and relevant code/commands in order |
| **Verify** | Observable checkpoint that must pass |
| **Next** | The next numbered step or selected capability |

Do not move to **Next** while **Verify** is failing. Replace example package names, fields, routes, and domain names with those recorded in your project workbook.

Code blocks show the relevant implementation at that step; add the matching `package` declaration and IDE-generated imports. When an API differs across Spring major versions, keep the version selected by Spring Initializr and confirm the linked official reference rather than mixing examples from another version.

“Where” always uses one of these exact forms:

```text
Browser: https://start.spring.io
Terminal (working directory): <project-root>
Create file: src/main/java/com/company/project/task/TaskService.java
Edit file: src/main/resources/application.yml
Create folder: src/main/resources/db/migration/
```

If a step has several actions, its **Repository action map** states separately where each file is created/edited and where each command is run. The code block immediately following a file instruction belongs in that file.

## Shared resources

- [Project workbook](docs/project-workbook.md) — copy into your project to track requirements, features, tests, and delivery.
- [Java and Spring Boot foundation](docs/java-spring-foundation.md) — prerequisites, Java syntax, generated files, Spring wiring, and the normal development loop.
- [Testing guide](docs/testing-guide.md) — concrete unit, MVC, persistence, and integration-test patterns.
- [Configuration guide](docs/configuration-guide.md) — YAML, environment variables, profiles, validated settings, and secrets.
- [Delivery guide](docs/delivery-guide.md) — package, run, containerize, verify in CI, migrate, deploy, and roll back.
- [Taskboard reference API](taskboard-api/README.md) — runnable example for the REST + SQL path.
- [Troubleshooting](docs/troubleshooting.md) — use when a build, startup, HTTP, database, or test step fails.
- [Production checklist](docs/production-checklist.md) — final gate for every application type.
- [Official references](docs/official-references.md) — primary Spring documentation used by this repository.

## The rule for using this repository

```text
Choose one path → build one useful feature → add one required capability
→ verify → repeat only for remaining requirements → production checklist
```

The reference example uses Spring Boot 4.1.0 and Java 17. For a new project, confirm supported versions in the [official Spring Boot system requirements](https://docs.spring.io/spring-boot/system-requirements.html).
