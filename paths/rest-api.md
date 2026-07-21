# Process: Build a REST API

[← Choose another type](../README.md) · [Working example](../taskboard-api/README.md) · [Testing](../docs/testing-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this process when a frontend, mobile app, or another service sends HTTP requests and expects JSON.

## Step 1 · Define one endpoint

> 📍 Create `<project-root>/PROJECT.md`. Copy the feature-sheet headings from [project-workbook.md](../docs/project-workbook.md) into it.

Record method, path, input, success response/status, errors, access, and stored data.

```text
POST /api/tasks
Request:  { "title": "Learn Spring", "dueDate": "2030-01-01" }
Success:  201 Created + TaskResponse + Location: /api/tasks/{id}
Errors:   400 invalid, 401 unauthenticated, 403 forbidden, 409 conflict
```

Before continuing, check: Another person can write success and failure tests from the contract without asking what the endpoint should do.

Continue to Step 2.

## Step 2 · Generate and run the foundation

> 📍 Use [Spring Initializr](https://start.spring.io/) in the browser. Run every command below in `<project-root>/`, the directory containing `pom.xml` and `mvnw`.

Select Maven, Java, Jar, Spring Web, Validation, and Actuator. Add only currently required [capabilities](../README.md#add-extra-capabilities-only-when-needed).

```bash
./mvnw clean verify
./mvnw spring-boot:run
curl http://localhost:8080/actuator/health
```

Before continuing, check: Build ends with `BUILD SUCCESS`; application starts; health returns `UP`.

Stop the application and continue to Step 3.

## Step 3 · Create the feature files

> 📍 Create `src/main/java/com/company/project/task/`, `src/main/java/com/company/project/task/dto/`, and `src/test/java/com/company/project/task/`. Replace `com/company/project` with the package selected in Initializr.

For a database-backed feature create:

```text
task/
├── Task.java
├── TaskRepository.java
├── TaskMapper.java
├── TaskService.java
├── TaskController.java
├── TaskNotFoundException.java
└── dto/
    ├── CreateTaskRequest.java
    └── TaskResponse.java
```

Without persistence, omit entity/repository. Keep the flow controller → service → repository/adapter.

```java
public record CreateTaskRequest(
    @NotBlank @Size(max = 120) String title,
    @FutureOrPresent LocalDate dueDate
) {}
```

Before continuing, check: Package declarations match directories and `./mvnw compile` passes.

Continue to Step 4.

## Step 4 · Implement the vertical slice

> 📍 Edit `src/main/java/com/company/project/task/Task.java`, `TaskRepository.java`, `TaskMapper.java`, `TaskService.java`, `TaskController.java`, and the files under `task/dto/`. Run commands in `<project-root>/`.

Implement in dependency order:

```text
entity → repository → DTOs → mapper → service → controller
```

Service:

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
        Task task = Task.create(request.title(), request.dueDate());
        return mapper.toResponse(repository.save(task));
    }
}
```

Controller:

```java
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<TaskResponse> create(
            @Valid @RequestBody CreateTaskRequest request) {
        TaskResponse result = service.create(request);
        return ResponseEntity.created(URI.create("/api/tasks/" + result.id()))
            .body(result);
    }
}
```

Before continuing, check: Start the application and call the endpoint:

```bash
curl -i -X POST http://localhost:8080/api/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Learn Spring","dueDate":"2030-01-01"}'
```

Confirm `201`, `Location`, response JSON, and stored/observable result.

Continue to Step 5.

## Step 5 · Make errors and optional capabilities explicit

> 📍 Edit `src/main/java/com/company/project/task/dto/CreateTaskRequest.java` and exceptions in `task/`. Create `src/main/java/com/company/project/common/error/ApiExceptionHandler.java`. Add a capability only in the folder named by its linked guide.

Map validation to `400`, missing data to `404`, permission to `401/403`, and conflict to `409` using `ProblemDetail` and `@RestControllerAdvice`. Then attach one required module:

- [Data storage](../capabilities/data-storage.md)
- [Security](../capabilities/security.md)
- [External API](../capabilities/external-api.md)
- [Messaging](../capabilities/messaging.md)
- [File storage](../capabilities/file-storage.md)
- [Caching](../capabilities/caching.md)

Before continuing, check: Valid JSON succeeds; blank/malformed input and every written expected failure return the contract status and safe body.

Continue to Step 6.

## Step 6 · Test and document

> 📍 Create tests in `src/test/java/com/company/project/task/`. Put manual HTTP calls in `<project-root>/requests.http`, and run the build in `<project-root>/`.

Follow the [testing guide](../docs/testing-guide.md). Add service unit tests, MVC tests, repository tests when persistence exists, and one critical integration test.

```bash
./mvnw clean verify
```

Before continuing, check: Clean build passes outside the IDE and manual happy/failure calls match the workbook.

Return to Step 1 for another required endpoint, or Step 7 when requirements are complete.

## Step 7 · Configure and deliver

> 📍 Edit `src/main/resources/application.yml`, `<project-root>/README.md`, and the deployment/CI file required by the chosen platform. Run packaging and smoke-test commands in `<project-root>/`.

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), then the [production checklist](../docs/production-checklist.md).

Before continuing, check: A clean environment can configure, migrate, start, health-check, and smoke-test the same artifact without source edits.

Release and monitor the application.
