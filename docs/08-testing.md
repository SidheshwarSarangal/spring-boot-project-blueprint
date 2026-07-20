# 08 · Testing

[← Project workflow](00-project-workflow.md) · [Repository home](../README.md) · [Testing gate](00-project-workflow.md#gate-7--test-the-slice)

## Test the smallest useful scope

```mermaid
flowchart TB
    E2E["Few end-to-end tests<br/>deployed journey"]
    Full["Integration tests<br/>full Spring context + real dependencies"]
    Slice["Slice tests<br/>MVC or JPA layer"]
    Unit["Many unit tests<br/>plain Java"]
    E2E --> Full --> Slice --> Unit
```

| Scope | Typical tool | Proves |
|---|---|---|
| Unit | JUnit + Mockito | Business logic in one class |
| MVC slice | `@WebMvcTest` + MockMvc | Routes, JSON, validation, status, error advice |
| JPA slice | `@DataJpaTest` | Entity mappings and repository queries |
| Integration | `@SpringBootTest` | Beans and layers work together |
| Production-like data | Testcontainers | Real database behavior |
| End to end | External client/browser | Running system and dependencies |

## Service unit test

```mermaid
flowchart LR
    Test["TaskServiceTest"] --> Real["Real TaskService"]
    Real --> MockRepo["Mock repository"]
    Real --> Mapper["Real mapper"]
    Test --> Assert["Result + interaction assertions"]
```

```java
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock TaskRepository repository;

    @Test
    void createsTask() {
        TaskMapper mapper = new TaskMapper();
        TaskService service = new TaskService(repository, mapper);
        // arrange → act → assert
    }
}
```

No Spring context is needed to test plain business logic.

## MVC slice

```mermaid
flowchart LR
    MockMvc["MockMvc request"] --> Controller["TaskController"]
    Controller --> MockService["@MockitoBean TaskService"]
    Controller --> Advice["Error advice"]
    MockMvc --> Assertions["status + headers + JSON"]
```

Use `@WebMvcTest` for the HTTP boundary. Mock the service because repository/database behavior is outside this slice.

## JPA slice

```mermaid
flowchart LR
    Test["@DataJpaTest"] --> Entity["Task mapping"]
    Test --> Repo["TaskRepository"]
    Repo --> Embedded[("Embedded test DB")]
    Test --> Rollback["Rollback after test"]
```

Spring Boot’s JPA slice configures entities and repositories, uses an embedded database when available, and rolls each test back by default.

## Full integration

```mermaid
sequenceDiagram
    participant Test
    participant App as Full application context
    participant HTTP as Real or mock web layer
    participant DB as Test database
    Test->>App: Start once
    Test->>HTTP: POST /api/tasks
    HTTP->>DB: INSERT
    DB-->>HTTP: Saved row
    HTTP-->>Test: 201 response
```

Use `@SpringBootTest` when the connection between layers is the thing being tested. Do not use it for every small branch.

## Arrange → Act → Assert

```mermaid
flowchart LR
    A["Arrange<br/>inputs + collaborators"] --> B["Act<br/>one behavior"]
    B --> C["Assert<br/>result + important side effects"]
```

## High-value test matrix

| Feature | Success | Boundary | Failure |
|---|---|---|---|
| Create task | Valid task saved | Max title, today due date | Blank title, past due date |
| Find task | Existing ID returned | First/last valid ID | Missing ID → 404 |
| List tasks | Page and filter | Empty page, last page | Invalid enum/page input |
| Update task | Fields changed | No-op/same values | Missing task, invalid state |
| Delete task | Row removed | Repeated call policy | Missing task |

## Test quality rules

- Assert behavior, not private implementation.
- Give each test isolated data.
- Make time and randomness controllable.
- Do not depend on test execution order.
- Use production database containers for database-specific queries.
- Keep fixtures small and readable.
- A failing test should explain what behavior broke.
