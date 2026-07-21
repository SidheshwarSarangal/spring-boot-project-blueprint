# First project tutorial: add a Taskboard summary

[← Application selector](../README.md) · [Tool setup](setup-guide.md) · [Taskboard reference](../taskboard-api/README.md) · [Troubleshooting](troubleshooting.md)

This tutorial takes you through one complete Spring Boot change with an observable result and an automated test. You will build the existing Taskboard API, run it, add `GET /api/tasks/summary`, test it, and verify the final application.

Allow 45–75 minutes for a first attempt. Work in a disposable copy or Git branch so you can compare your result with the original.

## Checkpoint 1 · Open and build the project

> 📍 Open `taskboard-api/`, the folder containing `pom.xml`, in your IDE and terminal.

```bash
java -version
javac -version
./mvnw clean verify
```

Windows users run `.\mvnw.cmd clean verify` in PowerShell.

Expected result:

```text
BUILD SUCCESS
```

If the command fails, stop and use the [setup guide](setup-guide.md) or [troubleshooting guide](troubleshooting.md). Do not add code to a project that does not yet build.

Before continuing, check: The unchanged project passes every test.

## Checkpoint 2 · Start and observe the existing application

> 📍 Keep Terminal 1 in `taskboard-api/`; use Terminal 2 for HTTP calls.

Terminal 1:

```bash
./mvnw spring-boot:run
```

Wait for `Started TaskboardApplication`. In Terminal 2:

```bash
curl -i http://localhost:8080/actuator/health
curl -i -X POST http://localhost:8080/api/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Finish the tutorial","dueDate":"2030-01-01"}'
```

Expected results:

- health returns `200` and `"status":"UP"`;
- create returns `201 Created`;
- the response contains an `id`, title, and `TODO` status.

Stop the application with `Ctrl+C`.

Before continuing, check: You have observed a real HTTP request reach the application and produce stored data.

## Checkpoint 3 · Understand the change before coding

> 📍 Open the existing files under `src/main/java/com/example/taskboard/task/`.

The new endpoint will return the total number of tasks:

```text
GET /api/tasks/summary
Success: 200 OK
Body: { "total": 3 }
```

The request will travel through:

```text
TaskController → TaskService → TaskRepository.count()
               → TaskSummaryResponse → JSON
```

Spring Data already supplies `count()` through `JpaRepository`, so no repository method is required.

Before continuing, check: You can name the route, response, service method, repository operation, and test assertion.

## Checkpoint 4 · Create the response type

> 📍 Create `src/main/java/com/example/taskboard/task/dto/TaskSummaryResponse.java`.

```java
package com.example.taskboard.task.dto;

public record TaskSummaryResponse(long total) {
}
```

Run:

```bash
./mvnw compile
```

Expected result: `BUILD SUCCESS`.

Before continuing, check: The filename, public record name, package declaration, and directory agree.

## Checkpoint 5 · Add the service behavior

> 📍 Open `src/main/java/com/example/taskboard/task/TaskService.java`.

Add this import with the other Taskboard DTO imports:

```java
import com.example.taskboard.task.dto.TaskSummaryResponse;
```

Add this method inside `TaskService`:

```java
@Transactional(readOnly = true)
public TaskSummaryResponse summarize() {
    return new TaskSummaryResponse(repository.count());
}
```

Run `./mvnw compile` again.

Before continuing, check: Compilation passes and the method only coordinates the repository and response—it does not know about HTTP.

## Checkpoint 6 · Expose the HTTP endpoint

> 📍 Open `src/main/java/com/example/taskboard/task/TaskController.java`.

Add the import:

```java
import com.example.taskboard.task.dto.TaskSummaryResponse;
```

Add this method inside `TaskController`:

```java
@GetMapping("/summary")
public TaskSummaryResponse summarize() {
    return service.summarize();
}
```

The literal `/summary` route does not conflict with `/{id}` because Spring selects the more specific mapping.

Run:

```bash
./mvnw compile
```

Before continuing, check: Compilation passes and the controller delegates instead of counting tasks itself.

## Checkpoint 7 · Add an MVC test

> 📍 Open `src/test/java/com/example/taskboard/task/TaskControllerTest.java`.

Add this import:

```java
import com.example.taskboard.task.dto.TaskSummaryResponse;
```

Add the static import for `get` beside the existing `post` import:

```java
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
```

Add this test inside `TaskControllerTest`:

```java
@Test
void returnsTaskSummary() throws Exception {
    when(service.summarize()).thenReturn(new TaskSummaryResponse(3));

    mockMvc.perform(get("/api/tasks/summary"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(3));
}
```

Run only this test class while correcting mistakes:

```bash
./mvnw -Dtest=TaskControllerTest test
```

Expected result: four tests run with no failures or errors.

Before continuing, check: The test proves the route, status, and JSON contract without starting a real server.

## Checkpoint 8 · Verify the completed vertical slice

> 📍 Run the full build, then start the application and call the new endpoint.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

In a second terminal create two tasks, then call:

```bash
curl -i http://localhost:8080/api/tasks/summary
```

Expected response shape:

```http
HTTP/1.1 200
Content-Type: application/json

{"total":2}
```

The number may be higher because the local file-backed database preserves tasks from earlier runs.

Before continuing, check: The clean build passes and the running application returns the actual stored count.

## Checkpoint 9 · Review and save the work

> 📍 Stop the application, then inspect the change from `taskboard-api/` or the repository root.

```bash
git status --short
git diff
```

You should see one new DTO plus edits to the service, controller, and controller test. Confirm no `target/`, database, IDE, token, or password file is staged.

You have completed the normal Spring Boot loop:

```text
define contract → add response type → add service behavior
→ expose entry point → test contract → run real request → review
```

Next, choose a [primary application type](../README.md#select-one-primary-application-type) or learn how to [adapt a runnable starter](starter-adaptation-guide.md) for your own project.
