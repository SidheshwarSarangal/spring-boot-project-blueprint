# Process: Build a GraphQL API

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this when clients query a typed schema and select fields. If fixed resources/status codes are the better contract, use [REST](rest-api.md).

## Repository action map

| Step | Exact location | Add or run there |
|---|---|---|
| 1 | Create `<project-root>/PROJECT.md`; create `src/main/resources/graphql/schema.graphqls` | Operation/schema contract |
| 2 | Browser: Initializr; Terminal: generated `<project-root>` | Generate/build/start |
| 3 | Edit schema; create `src/main/java/com/company/project/task/` GraphQL files | Input/payload/controller/service code |
| 4 | Create/edit data-loader/query/error/security files in feature/common packages | Bounds, batching, GraphQL errors |
| 5 | Create/edit selected capability package | Data/security/provider/file/cache |
| 6 | Create matching `src/test/java/.../task/`; edit schema docs/config/CI | Tests and delivery |

**Beginner actions by step:** 1 → [A workbook](../docs/beginner-execution-guide.md#action-a-create-the-working-repository-and-workbook), [I schema resource](../docs/beginner-execution-guide.md#action-i-create-a-resource-file); 2 → [B generate](../docs/beginner-execution-guide.md#action-b-generate-the-spring-project-in-the-browser), [D terminal](../docs/beginner-execution-guide.md#action-d-run-a-command-in-the-correct-terminal); 3–5 → [E create files](../docs/beginner-execution-guide.md#action-e-create-a-java-package-and-file), [F add code](../docs/beginner-execution-guide.md#action-f-put-a-provided-java-code-block-into-a-file), [I schema](../docs/beginner-execution-guide.md#action-i-create-a-resource-file), [J call](../docs/beginner-execution-guide.md#action-j-start-the-application-and-call-it); 6 → [K tests](../docs/beginner-execution-guide.md#action-k-create-and-run-a-test), [M checkpoint](../docs/beginner-execution-guide.md#action-m-save-a-clean-checkpoint-with-git).

## Step 1 · Define one query or mutation

**What:** Produce the schema contract, authorization, and error behavior.

**Where:** Feature sheet in `PROJECT.md` and `src/main/resources/graphql/schema.graphqls`.

**Do:** Start with one operation:

```graphql
type Task { id: ID!, title: String!, status: TaskStatus! }
enum TaskStatus { TODO, DONE }
input CreateTaskInput { title: String!, dueDate: String }
type Query { task(id: ID!): Task }
type Mutation { createTask(input: CreateTaskInput!): Task! }
```

Record missing data, validation, permission, and conflict behavior.

**Verify:** Schema parses conceptually and the client-visible fields/types are intentional.

**Next:** Step 2.

## Step 2 · Generate and run the foundation

**What:** Start a GraphQL-capable Spring application.

**Where:** Spring Initializr and project root.

**Do:** Select Spring for GraphQL, Spring Web, Validation, and Actuator; add only required capabilities.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

**Verify:** Application starts and GraphQL endpoint is registered without schema errors.

**Next:** Step 3.

## Step 3 · Create operation types, controller, and service

**What:** Connect schema operation → controller → service.

**Where:**

```text
src/main/resources/graphql/schema.graphqls
src/main/java/com/company/project/task/
├── CreateTaskInput.java
├── TaskPayload.java
├── TaskGraphqlController.java
└── TaskService.java
```

**Do:**

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

**Verify:** `./mvnw compile` passes; execute one query/mutation and confirm selected response fields.

**Next:** Step 4.

## Step 4 · Bound data loading and errors

**What:** Prevent expensive/unbounded queries and return useful GraphQL errors.

**Where:** Schema, data loaders/query layer, GraphQL exception resolver, security configuration.

**Do:** Batch related loading to prevent N+1; bound depth/complexity/aliases/result sizes; map expected domain exceptions to stable GraphQL error extensions; authorize both operations and returned records/fields.

**Verify:** Relationship query uses a bounded/batched access pattern; excessive query is rejected; missing/invalid/forbidden outcomes match the contract.

**Next:** Step 5.

## Step 5 · Attach required capabilities

**What:** Add only infrastructure used by the operation.

**Where:** Selected capability packages.

**Do:** Choose [data storage](../capabilities/data-storage.md), [security](../capabilities/security.md), [external API](../capabilities/external-api.md), [file storage](../capabilities/file-storage.md), or [caching](../capabilities/caching.md). Use a separate upload flow for files unless the schema/provider contract explicitly defines one.

**Verify:** Capability failure/permission behavior appears as bounded, safe GraphQL results.

**Next:** Step 6.

## Step 6 · Test and deliver

**What:** Prove schema compatibility, behavior, limits, and delivery.

**Where:** `src/test/java`, schema tests, configuration, CI/deployment, client documentation.

**Do:** Test query/mutation, validation, missing data, field/record authorization, complexity limits, batching/N+1, and schema compatibility using the [testing guide](../docs/testing-guide.md).

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

**Verify:** Clean build passes; schema is published; queries are bounded; authorized results are correct in a clean environment.

**Next:** Release, or return to Step 1 for another operation.
