# Path: GraphQL API

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once before Step 1.

Choose this when clients query a typed schema and select the fields they need. If fixed HTTP resources are sufficient, use the [REST API path](rest-api.md).

## 1. Define the first operation

Write one query or mutation, its schema types, authorization, validation, and expected errors in the [project workbook](../docs/project-workbook.md).

```graphql
type Task { id: ID!, title: String!, status: TaskStatus! }
type Query { task(id: ID!): Task }
type Mutation { createTask(input: CreateTaskInput!): Task! }
```

## 2. Generate the project

Select Spring for GraphQL, Spring Web, Validation, and Actuator. Add [data storage](../capabilities/data-storage.md) and [security](../capabilities/security.md) only when required.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Continue only after the untouched application starts.

## 3. Build one operation

```text
schema → input/output types → @QueryMapping/@MutationMapping
→ service → repository/adapter → GraphQL result
```

```text
src/main/resources/graphql/schema.graphqls
src/main/java/com/company/project/task/
├── TaskGraphqlController.java
├── TaskService.java
├── TaskInput.java
└── TaskPayload.java
```

1. Keep resolvers/controllers thin and business rules in services.
2. Validate mutation input and return stable application errors.
3. Authorize operations and field/data ownership.
4. Batch related data loading to prevent N+1 queries.
5. Bound query depth, complexity, aliases, and result size.
6. Evolve the schema with deprecation instead of breaking clients.

Checkpoint: execute one query/mutation through the GraphQL endpoint and verify the response shape and one invalid-input error before adding relationships.

## 4. Attach required capabilities

- [Data storage](../capabilities/data-storage.md)
- [Security](../capabilities/security.md)
- [External API](../capabilities/external-api.md)
- [File storage](../capabilities/file-storage.md)—usually through a separate upload flow
- [Caching](../capabilities/caching.md)

## 5. Verify

Test valid query/mutation, validation, unauthorized fields/data, missing records, complexity limits, batching/N+1 behavior, and schema compatibility.

```bash
./mvnw clean verify
```

## 6. Finish

Repeat for the next required operation, publish schema/client guidance, then complete the [production checklist](../docs/production-checklist.md).

Done means the schema is compatible, queries are bounded, data loading is efficient, and authorization applies to returned data—not only the operation name.
