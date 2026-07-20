# Testing guide

[← Application selector](../README.md) · [Working tests](../taskboard-api/src/test/java/com/example/taskboard) · [Troubleshooting](troubleshooting.md)

Test observable behavior at the smallest useful scope. Do not load the full application for every test.

## 1. Turn the feature sheet into cases

For each feature copy its success, invalid, missing, forbidden, conflict, external-failure, and retry cases from the [project workbook](project-workbook.md). Each test should prove one behavior.

## 2. Choose the test scope

| Need to prove | Test scope |
|---|---|
| Business rule in one service | Unit test with mocked collaborators |
| HTTP path, JSON, validation, status/error | MVC test with MockMvc |
| Entity mapping or repository query | JPA test |
| Several real application components | Integration test |
| Provider/broker/database behavior | Integration test with stub/Testcontainers |

## 3. Service unit test

Place it in the matching package under `src/test/java`.

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

Mock boundaries, not simple value objects. Assert the returned result/state and verify an important collaborator call only when it is part of the behavior.

## 4. MVC test

Use the MVC test annotation supported by the selected Spring Boot version and `MockMvc`. Import the global error handler when the slice does not discover it automatically.

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

Use `@SpringBootTest` only where the real bean graph matters. Start external dependencies with Testcontainers or a stub and supply their connection settings dynamically. Keep the test isolated and deterministic.

Critical integration tests should prove:

- application wiring and startup;
- migration plus persistence;
- security filter behavior;
- provider/broker protocol assumptions;
- one essential end-to-end business flow.

## 7. Test non-HTTP paths

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

- Arrange, act, assert.
- Give tests behavior names such as `rejectsPastDueDate`.
- Do not depend on execution order, current time, random ports, or shared leftover data without controlling them.
- Use fixed clocks/IDs where business logic depends on them.
- Never call production services from normal automated tests.
- A test must fail when the behavior it protects is broken.

## 9. Run the gates

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
