# Testing guide

[← Application selector](../README.md) · [Working tests](../taskboard-api/src/test/java/com/example/taskboard) · [Troubleshooting](troubleshooting.md)

Test results that a user or another part of the program can observe. Use the smallest test that proves the result; starting the whole application for every test makes the suite slower and harder to debug.

If creating/running test files is new, use [Action K](beginner-execution-guide.md#action-k-create-and-run-a-test); for failures use [Action L](beginner-execution-guide.md#action-l-fix-the-first-compile-or-startup-error).

## 1. Turn the feature sheet into cases

> 📍 Open the current feature in `<project-root>/PROJECT.md` and write the test cases beneath its acceptance criteria.

For each feature copy its success, invalid, missing, forbidden, conflict, external-failure, and retry cases from the [project workbook](project-workbook.md). Each test should prove one behavior.

## 2. Choose the test scope

> 📍 Record the chosen scope beside each test case in `<project-root>/PROJECT.md`.

| Need to prove | Test scope |
|---|---|
| Business rule in one service | Unit test with fake versions of its dependencies |
| HTTP path, JSON, validation, status/error | MVC test with MockMvc |
| Entity mapping or repository query | JPA test |
| Several real application parts working together | Integration test |
| Real provider, broker, or database behavior | Integration test with a stub or Testcontainers |

## 3. Service unit test

> 📍 Create `src/test/java/com/company/project/task/TaskServiceTest.java`.

```java
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock TaskRepository repository;
    TaskMapper mapper = new TaskMapper();
    TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService(repository, mapper);
    }

    @Test
    void returnsTaskWhenItExists() {
        Task task = new Task("Learn testing", LocalDate.of(2030, 1, 1));
        when(repository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponse result = service.findById(1L);

        assertThat(result.title()).isEqualTo("Learn testing");
    }
}
```

Replace repositories and external services with controlled fakes. Keep simple data objects real. Check the returned result or changed state; check a dependency call only when that call is part of the required behavior.

## 4. MVC test

> 📍 Create `src/test/java/com/company/project/task/TaskControllerTest.java`.

Use the MVC test annotation supported by your Spring Boot version and `MockMvc`. If expected error responses are missing, include the global error handler in the test configuration.

```java
@WebMvcTest(TaskController.class)
class TaskControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean TaskService service;

    @Test
    void rejectsBlankTitle() throws Exception {
        mvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\"}"))
            .andExpect(status().isBadRequest());
    }
}
```

Test the public contract, not controller implementation details.

## 5. Repository test

> 📍 Create `src/test/java/com/company/project/task/TaskRepositoryTest.java`.

```java
@DataJpaTest
class TaskRepositoryTest {
    @Autowired TaskRepository repository;

    @Test
    void filtersByStatus() {
        repository.save(new Task("One", LocalDate.of(2030, 1, 1)));

        Page<Task> result = repository.findAllByStatus(
            TaskStatus.TODO, PageRequest.of(0, 20)
        );

        assertThat(result.getContent()).hasSize(1);
    }
}
```

Use the production database type through Testcontainers for database-specific queries or critical mappings.

## 6. Integration test

> 📍 Create `src/test/java/com/company/project/TaskApplicationTest.java`; keep it under the application’s base package.

Use `@SpringBootTest` only when you need to prove that real Spring-managed objects connect correctly. Start external dependencies with Testcontainers or a stub and provide their connection settings to the test. The result must not depend on test order or leftover data.

Critical integration tests should prove:

- application wiring and startup;
- migration plus persistence;
- security filter behavior;
- provider/broker protocol assumptions;
- one essential end-to-end business flow.

## 7. Test non-HTTP paths

> 📍 Put each test in the matching feature package under `src/test/java/`; use the row for the selected application path.

| Path | Important tests |
|---|---|
| Web page | view name, model, validation errors, redirect, CSRF/access |
| Background job | trigger delegates, duplicate, retry exhaustion, restart |
| Event consumer | valid/invalid event, duplicate, acknowledgement, dead letter |
| Batch | reader/processor/writer, skip/retry, restart, job parameters |
| GraphQL | query/mutation, field authorization, complexity, N+1 batching |
| Real-time | connect/authenticate, publish/receive, reconnect, slow client |
| CLI | argument validation, stdout/stderr, exit code, safe rerun |

## 8. Keep tests trustworthy

> 📍 Apply these rules to every file under `src/test/java/`.

- Arrange, act, assert.
- Give tests behavior names such as `rejectsPastDueDate`.
- Control test order, time, ports, and data instead of depending on whatever happens to be available.
- Use fixed clocks/IDs where business logic depends on them.
- Never call production services from normal automated tests.
- A test must fail when the behavior it protects is broken.

## 9. Run the gates

> 📍 Run these commands in `<project-root>/`, the folder containing `pom.xml` and `mvnw`.

During work:

```bash
./mvnw -Dtest=TaskServiceTest test
./mvnw test
```

Before commit/share:

```bash
./mvnw clean verify
```

Completion: the clean build is repeatable outside the IDE and every important feature failure has an automated check at the smallest suitable scope.
