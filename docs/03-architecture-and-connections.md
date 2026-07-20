# 03 · Architecture and connections

[← Setup](02-project-setup.md) · [README](../README.md) · [Next: Build a feature →](04-build-a-feature.md)

## Complete request lifecycle

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

## Who may call whom?

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

## Spring bean graph

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

## MVC routing

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

## Repository implementation

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

## Transaction boundary

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

## Cross-cutting concerns

```mermaid
flowchart LR
    Request["Request"] --> Filters["Filters<br/>security, correlation"]
    Filters --> Controller["Controller"]
    Controller --> Interceptor["MVC interceptor<br/>web concerns"]
    Controller --> Advice["Controller advice<br/>errors"]
    Controller --> Service["Service"]
    Service --> AOP["AOP proxy<br/>transactions, metrics"]
```

Use the boundary designed for the concern; do not duplicate logging, error conversion, or authorization in every controller method.

## Connecting external services

```mermaid
flowchart LR
    Controller --> Service
    Service --> Port["Interface<br/>NotificationSender"]
    Port --> Email["Email adapter"]
    Port --> Fake["Test fake"]
    Email --> Provider["External provider"]
```

Hide provider-specific code behind an application-owned interface. The service knows the capability it needs, not the vendor SDK details.
