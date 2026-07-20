# Process: Build a REST API

[← Choose another type](../README.md) · [Working example](../taskboard-api/README.md) · [Testing](../docs/testing-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this process when a frontend, mobile app, or another service sends HTTP requests and expects JSON.

## Repository action map

| Step | Exact location | Add or run there |
|---|---|---|
| 1 | Create `<project-root>/PROJECT.md` | Endpoint contract and acceptance cases |
| 2 | Browser: Initializr; Terminal: generated `<project-root>` | Generate; run `./mvnw ...` and `curl` |
| 3 | Create `src/main/java/com/company/project/task/` and `task/dto/` | Entity/repository/service/controller/DTO files |
| 4 | Edit `task/TaskService.java`, `task/TaskController.java`; Terminal: `<project-root>` | Add vertical-slice code; run request |
| 5 | Edit DTO/service; create `src/main/java/com/company/project/common/error/ApiExceptionHandler.java` | Validation/exception mappings/capability code |
| 6 | Create matching `src/test/java/com/company/project/task/`; edit `<project-root>/README.md` or `requests.http` | Tests and runnable examples |
| 7 | Edit `src/main/resources/application.yml`; create CI/deployment files at project root/platform location | Configure, package, deploy |

## Step 1 · Define one endpoint

**What:** Produce a testable HTTP contract for one user action.

**Where:** Copy [project-workbook.md](../docs/project-workbook.md) into the new project as `PROJECT.md`; fill one feature sheet.

**Do:** Record method, path, input, success response/status, errors, access, and stored data.

```text
POST /api/tasks
Request:  { "title": "Learn Spring", "dueDate": "2030-01-01" }
Success:  201 Created + TaskResponse + Location: /api/tasks/{id}
Errors:   400 invalid, 401 unauthenticated, 403 forbidden, 409 conflict
```

**Verify:** Another person can write success and failure tests from the contract without asking what the endpoint should do.

**Next:** Step 2.

## Step 2 · Generate and run the foundation

**What:** Create an untouched application that builds and starts.

**Where:** [Spring Initializr](https://start.spring.io/), then the extracted project root containing `pom.xml`.

**Do:** Select Maven, Java, Jar, Spring Web, Validation, and Actuator. Add only currently required [capabilities](../README.md#add-capabilities-only-when-the-selected-path-asks-for-them).

```bash
./mvnw clean verify
./mvnw spring-boot:run
curl http://localhost:8080/actuator/health
```

**Verify:** Build ends with `BUILD SUCCESS`; application starts; health returns `UP`.

**Next:** Stop the application and continue to Step 3.

## Step 3 · Create the feature files

**What:** Create the package structure and dependency direction for one endpoint.

**Where:** `src/main/java/<base-package>/<feature>/` and matching `src/test/java/` package.

**Do:** For a database-backed feature create:

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

**Verify:** Package declarations match directories and `./mvnw compile` passes.

**Next:** Step 4.

## Step 4 · Implement the vertical slice

**What:** Make the endpoint work through every required layer.

**Where:** Files created in Step 3. Use the [core implementation guide](../docs/core-guide.md) for complete entity, repository, mapper, service, controller, and error examples.

**Do:** Implement in dependency order:

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

**Verify:** Start the application and call the endpoint:

```bash
curl -i -X POST http://localhost:8080/api/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Learn Spring","dueDate":"2030-01-01"}'
```

Confirm `201`, `Location`, response JSON, and stored/observable result.

**Next:** Step 5.

## Step 5 · Make errors and optional capabilities explicit

**What:** Complete failure behavior and attach only required infrastructure.

**Where:** Request DTOs, service exceptions, `common/error/ApiExceptionHandler.java`, and selected capability package.

**Do:** Map validation to `400`, missing data to `404`, permission to `401/403`, and conflict to `409` using `ProblemDetail` and `@RestControllerAdvice`. Then attach one required module:

- [Data storage](../capabilities/data-storage.md)
- [Security](../capabilities/security.md)
- [External API](../capabilities/external-api.md)
- [Messaging](../capabilities/messaging.md)
- [File storage](../capabilities/file-storage.md)
- [Caching](../capabilities/caching.md)

**Verify:** Valid JSON succeeds; blank/malformed input and every written expected failure return the contract status and safe body.

**Next:** Step 6.

## Step 6 · Test and document

**What:** Create repeatable proof of the contract.

**Where:** Matching packages under `src/test/java`; `requests.http` or project README for manual calls.

**Do:** Follow the [testing guide](../docs/testing-guide.md). Add service unit tests, MVC tests, repository tests when persistence exists, and one critical integration test.

```bash
./mvnw clean verify
```

**Verify:** Clean build passes outside the IDE and manual happy/failure calls match the workbook.

**Next:** Return to Step 1 for another required endpoint, or Step 7 when requirements are complete.

## Step 7 · Configure and deliver

**What:** Produce a configurable, observable, deployable artifact.

**Where:** `application.yml`, environment configuration, CI/deployment files, and project README.

**Do:** Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), then the [production checklist](../docs/production-checklist.md).

**Verify:** A clean environment can configure, migrate, start, health-check, and smoke-test the same artifact without source edits.

**Next:** Release and monitor the application.
