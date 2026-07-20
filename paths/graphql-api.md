# Process: Build a GraphQL API

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this when clients query a typed schema and select fields. If fixed resources/status codes are the better contract, use [REST](rest-api.md).

## Step 1 · Define one query or mutation

> 📍 Edit `<project-root>/PROJECT.md`, section **5. Feature sheet**; create/edit `src/main/resources/graphql/schema.graphqls`.

Start with one operation:

```graphql
type Task { id: ID!, title: String!, status: TaskStatus! }
enum TaskStatus { TODO, DONE }
input CreateTaskInput { title: String!, dueDate: String }
type Query { task(id: ID!): Task }
type Mutation { createTask(input: CreateTaskInput!): Task! }
```

Record missing data, validation, permission, and conflict behavior.

Before continuing, check: Schema parses conceptually and the client-visible fields/types are intentional.

Continue to Step 2.

## Step 2 · Generate and run the foundation

> 📍 Open [Spring Initializr](https://start.spring.io/) in the browser. After extracting the project, open a terminal in `<project-root>/`, the folder containing `pom.xml` and `mvnw`.

Select Spring for GraphQL, Spring Web, Validation, and Actuator; add only required capabilities.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Before continuing, check: Application starts and GraphQL endpoint is registered without schema errors.

Continue to Step 3.

## Step 3 · Create operation types, controller, and service

> 📍 Create the schema and Java files at the paths below. Replace `com/company/project` with the package selected in Initializr.

```text
src/main/resources/graphql/schema.graphqls
src/main/java/com/company/project/task/
├── CreateTaskInput.java
├── TaskPayload.java
├── TaskGraphqlController.java
└── TaskService.java
```

```java
public record CreateTaskInput(
    @NotBlank @Size(max = 120) String title,
    LocalDate dueDate
) {}

@Controller
class TaskGraphqlController {
    private final TaskService service;

    TaskGraphqlController(TaskService service) {
        this.service = service;
    }

    @QueryMapping
    TaskPayload task(@Argument Long id) {
        return service.findById(id);
    }

    @MutationMapping
    TaskPayload createTask(@Argument @Valid CreateTaskInput input) {
        return service.create(input);
    }
}
```

Before continuing, check: `./mvnw compile` passes; execute one query/mutation and confirm selected response fields.

Continue to Step 4.

## Step 4 · Bound data loading and errors

> 📍 Edit `src/main/resources/graphql/schema.graphqls`. Create `src/main/java/com/company/project/task/TaskDataLoader.java` and `src/main/java/com/company/project/common/graphql/GraphqlExceptionResolver.java`. Edit `src/main/java/com/company/project/security/SecurityConfiguration.java` when the operation is protected.

Batch related loading to prevent N+1; bound depth/complexity/aliases/result sizes; map expected domain exceptions to stable GraphQL error extensions; authorize both operations and returned records/fields.

Before continuing, check: Relationship query uses a bounded/batched access pattern; excessive query is rejected; missing/invalid/forbidden outcomes match the contract.

Continue to Step 5.

## Step 5 · Attach required capabilities

> 📍 Create only the linked capability folder under `src/main/java/com/company/project/`. Edit `src/main/java/com/company/project/task/TaskService.java` to call its interface.

Choose [data storage](../capabilities/data-storage.md), [security](../capabilities/security.md), [external API](../capabilities/external-api.md), [file storage](../capabilities/file-storage.md), or [caching](../capabilities/caching.md). Use a separate upload flow for files unless the schema/provider contract explicitly defines one.

Before continuing, check: Capability failure/permission behavior appears as bounded, safe GraphQL results.

Continue to Step 6.

## Step 6 · Test and deliver

> 📍 Create tests under `src/test/java/com/company/project/task/`; edit GraphQL schema/docs, `application.yml`, root CI/deployment files; run commands at `<project-root>`.

Test query/mutation, validation, missing data, field/record authorization, complexity limits, batching/N+1, and schema compatibility using the [testing guide](../docs/testing-guide.md).

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

Before continuing, check: Clean build passes; schema is published; queries are bounded; authorized results are correct in a clean environment.

Release, or return to Step 1 for another operation.
