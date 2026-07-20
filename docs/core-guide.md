# Build the core application

[← Start here](../README.md) · [Project workbook](project-workbook.md) · [Working example](../taskboard-api/README.md)

Use this guide when the chosen application path needs a database-backed REST API. For an API without persistence, omit the entity and repository and let the service call the required adapter.

**Beginner physical actions used here:** [create Java files](beginner-execution-guide.md#action-e-create-a-java-package-and-file), [put code in the named file](beginner-execution-guide.md#action-f-put-a-provided-java-code-block-into-a-file), [edit YAML](beginner-execution-guide.md#action-h-edit-yaml-configuration), [run/call the app](beginner-execution-guide.md#action-j-start-the-application-and-call-it), and [create tests](beginner-execution-guide.md#action-k-create-and-run-a-test).

## Core implementation step map

| Step | What | Where | Do | Verify | Next |
|---|---|---|---|---|---|
| 1 | HTTP contract | `PROJECT.md` | Write input/output/status/errors | Test can be written | 2 |
| 2 | Feature package | `src/main/java/<base>/<feature>` | Create named files | Packages compile | 3 |
| 3 | Entity | `<feature>/Task.java` | Map stored state | Mapping compiles | 4 |
| 4 | Repository | `<feature>/TaskRepository.java` | Extend JPA repository/add bounded query | Repository test can load | 5 |
| 5 | DTOs | `<feature>/dto` | Define validated input/safe output | JSON boundary is explicit | 6 |
| 6 | Mapper | `<feature>/TaskMapper.java` | Convert entity to response | Mapping result correct | 7 |
| 7 | Service | `<feature>/TaskService.java` | Add rules/transaction/repository calls | Unit test passes | 8 |
| 8 | Controller | `<feature>/TaskController.java` | Bind/validate/call/respond | Valid request reaches service | 9 |
| 9 | Errors | Exception + `common/error` | Map expected exceptions | Intended 4xx bodies return | 10 |
| 10 | Local DB | `application-local.yml` | Configure disposable local DB | App connects/starts | 11 |
| 11 | Real request | Terminal/`requests.http` | Start and call endpoint | HTTP + stored result correct | 12 |
| 12 | Automated proof | `src/test/java` | Add focused tests; clean verify | Clean build passes | Capability/delivery |

The numbered sections below contain the relevant code. Stop when **Verify** fails.

## 1. Write the contract

Define one operation before writing Java:

```text
POST /api/tasks
Request:  { "title": "Learn Spring", "dueDate": "2030-01-01" }
Success:  201 Created + TaskResponse + Location header
Invalid:  400 Bad Request
```

Decide which fields the client may send, which fields the server owns, what is stored, and which failures are expected.

## 2. Create the package

Keep the generated `*Application.java` above all other packages so component scanning finds them.

```text
com.example.taskboard/
├── TaskboardApplication.java
├── common/error/ApiExceptionHandler.java
└── task/
    ├── Task.java
    ├── TaskStatus.java
    ├── TaskRepository.java
    ├── TaskMapper.java
    ├── TaskService.java
    ├── TaskController.java
    └── dto/
```

Group by feature. A controller handles HTTP, a service owns the use case, a repository accesses data, an entity maps stored state, and DTOs define the API boundary.

## 3. Create the entity

```java
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status;

    private LocalDate dueDate;

    protected Task() {}

    public Task(String title, LocalDate dueDate) {
        this.title = title;
        this.dueDate = dueDate;
        this.status = TaskStatus.TODO;
    }

    // getters and methods that change valid entity state
}
```

Use `EnumType.STRING`. Keep a protected no-argument constructor for JPA. Do not return entities directly from controllers.

## 4. Create the repository

```java
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByStatus(TaskStatus status, Pageable pageable);
}
```

`JpaRepository` already supplies standard CRUD operations. Add a custom query only for a current use case, and paginate any collection that can grow.

## 5. Create API DTOs

```java
public record CreateTaskRequest(
    @NotBlank @Size(max = 120) String title,
    @FutureOrPresent LocalDate dueDate
) {}
```

```java
public record TaskResponse(
    Long id,
    String title,
    TaskStatus status,
    LocalDate dueDate
) {}
```

Request DTOs contain client-writable fields and boundary validation. Response DTOs contain only data the client may see.

## 6. Map entity to response

```java
@Component
public class TaskMapper {
    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
            task.getId(), task.getTitle(), task.getStatus(), task.getDueDate()
        );
    }
}
```

Mapping should transform shapes, not make permission or business decisions.

## 7. Implement the service

```java
@Service
public class TaskService {
    private final TaskRepository repository;
    private final TaskMapper mapper;

    public TaskService(TaskRepository repository, TaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public TaskResponse create(CreateTaskRequest request) {
        Task task = new Task(request.title(), request.dueDate());
        return mapper.toResponse(repository.save(task));
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        Task task = repository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        return mapper.toResponse(task);
    }
}
```

Use constructor injection. Put the transaction around the complete service operation. The service loads state, checks rules, changes state, calls repositories or integrations, and returns an application result.

## 8. Expose the controller

```java
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @Valid @RequestBody CreateTaskRequest request) {
        TaskResponse created = service.create(request);
        URI location = URI.create("/api/tasks/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public TaskResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }
}
```

The controller binds HTTP input, calls the service, and selects HTTP status and headers. It should not contain database access or business rules.

## 9. Handle expected errors

```java
public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super("Task " + id + " was not found");
    }
}
```

```java
@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(TaskNotFoundException.class)
    ProblemDetail handleNotFound(TaskNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Task not found");
        problem.setDetail(exception.getMessage());
        return problem;
    }
}
```

Also translate validation failures into a stable `400` response. Use specific exceptions for expected cases; log unexpected failures server-side and return a safe generic `500` response.

## 10. Configure the local database

For a disposable local example:

```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/app
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: update
    open-in-view: false
```

For a shared database, use PostgreSQL or the required database, environment-supplied connection settings, and Flyway or Liquibase migrations. Do not use `ddl-auto: update` in production.

## 11. Verify the feature

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

```bash
curl -i -X POST http://localhost:8080/api/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Learn Spring","dueDate":"2030-01-01"}'
```

Confirm the status, JSON, `Location` header, and database row. Then try invalid input and a missing ID.

## 12. Add focused tests

| What must work | Smallest useful test |
|---|---|
| Business rule or service branch | Unit test with mocked collaborators |
| Route, JSON, validation, error response | MVC slice test |
| Entity mapping or custom query | JPA slice test |
| Critical path across real components | Integration test |

Name tests by behavior, arrange the state, perform one action, and assert the observable result. The clean Maven build is the final check.

## Core completion gate

- A real request reaches controller → service → repository → database.
- The API uses separate request/response DTOs.
- Validation and expected errors have stable responses.
- Transactions surround service use cases.
- Growing lists are paginated.
- The clean build passes.

When all six are true, update the [project workbook](project-workbook.md), then add the next required feature/capability or continue to the [production checklist](production-checklist.md).
