# 03 · Architecture and connections

[← Project workflow](00-project-workflow.md) · [Repository home](../README.md) · [File skeleton gate](00-project-workflow.md#gate-4--create-the-package-and-file-skeleton)

| Before you act | Details |
|---|---|
| What | Decide which class owns each responsibility and which direction dependencies point. |
| Where | Feature package under `src/main/java/<base-package>/<feature>/`. |
| Input | One written API operation and its data needs. |
| Finish when | Every required class has one role and dependencies point controller → service → repository. |

> **Terms:** A Spring **bean** is an object created and connected by Spring. **Dependency injection** means a class receives the collaborators it needs through its constructor. `ApplicationContext` is Spring’s container of beans. A **transaction** groups database work so it commits together or rolls back together.

## Trace the request before creating classes

```mermaid
sequenceDiagram
    actor Client
    participant Filter as Security / filters
    participant MVC as DispatcherServlet
    participant C as TaskController
    participant S as TaskService
    participant R as TaskRepository
    participant JPA as Hibernate / JPA
    participant DB as Database

    Client->>Filter: POST /api/tasks + JSON
    Filter->>MVC: Allowed request
    MVC->>MVC: Match route + deserialize + validate
    MVC->>C: CreateTaskRequest
    C->>S: create(request)
    S->>S: Apply business rules
    S->>R: save(task)
    R->>JPA: Persist entity
    JPA->>DB: INSERT
    DB-->>JPA: Generated ID
    JPA-->>R: Saved entity
    R-->>S: Task
    S-->>C: TaskResponse
    C-->>Client: 201 Created + JSON
```

## Set the dependency direction

```mermaid
flowchart TB
    Controller["Controller"] --> Service["Service"]
    Service --> Repository["Repository"]
    Service --> Mapper["Mapper"]
    Repository --> Entity["Entity"]
    Mapper --> Entity
    Mapper --> DTO["DTO"]
    Controller --> DTO
    Error["Global error handler"] -.-> Controller
```

Keep dependency arrows pointing inward. A repository should not call a controller; an entity should not build an HTTP response.

## Connect classes through constructors

```mermaid
flowchart LR
    Context["ApplicationContext"] --> C["TaskController bean"]
    Context --> S["TaskService bean"]
    Context --> M["TaskMapper bean"]
    Context --> R["Generated TaskRepository bean"]
    C --> S
    S --> M
    S --> R
```

| Type | How it becomes available |
|---|---|
| Controller | Component scanning finds `@RestController` |
| Service | Component scanning finds `@Service` |
| Mapper | Component scanning finds `@Component` |
| Repository | Spring Data creates an implementation for the interface |
| DataSource | Boot auto-configures it from driver + properties |
| Entity manager | Boot/JPA auto-configuration creates it |

## Map HTTP to the controller

```mermaid
flowchart TD
    Req["GET /api/tasks/7"] --> Dispatcher["DispatcherServlet"]
    Dispatcher --> Mapping["HandlerMapping"]
    Mapping --> Match["TaskController.findById"]
    Match --> Bind["Path '7' → Long id"]
    Bind --> Invoke["Invoke method"]
    Invoke --> Jackson["TaskResponse → JSON"]
```

You do not call the controller yourself. Spring MVC finds the matching method and prepares its arguments.

## Declare the repository interface

You write:

```java
interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByStatus(TaskStatus status, Pageable pageable);
}
```

Spring Data provides:

```mermaid
flowchart LR
    Interface["TaskRepository interface"] --> Parser["Method-name query parser"]
    Base["JpaRepository operations"] --> Proxy["Runtime repository proxy"]
    Parser --> Proxy
    Proxy --> EntityManager["EntityManager"]
    EntityManager --> DB[("Database")]
```

No handwritten implementation is needed for standard CRUD or supported derived queries.

## Place the transaction around the use case

```mermaid
sequenceDiagram
    participant C as Controller
    participant Proxy as Transaction proxy
    participant S as Service method
    participant DB as Database

    C->>Proxy: service.update(...)
    Proxy->>DB: BEGIN
    Proxy->>S: Run use case
    S->>DB: SELECT + UPDATE
    alt Method completes
        Proxy->>DB: COMMIT
        Proxy-->>C: Response
    else Runtime exception
        Proxy->>DB: ROLLBACK
        Proxy-->>C: Exception
    end
```

Place `@Transactional` around a business use case, usually on a public service method. It is proxy-driven; casually calling a transactional method from another method in the same object can bypass the proxy.

**Next:** Return to [Workflow Gate 5](00-project-workflow.md#gate-5--implement-one-vertical-slice). Add external providers only through [Gate 8](00-project-workflow.md#gate-8--add-optional-capabilities-only-when-required).
