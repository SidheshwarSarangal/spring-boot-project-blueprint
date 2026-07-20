# Path: REST API

[← Choose another type](../README.md) · [Working Taskboard example](../taskboard-api/README.md) · [Troubleshooting](../docs/troubleshooting.md)

Choose this when clients send HTTP requests and expect JSON responses.

## 1. Define the first endpoint

Copy the [project workbook](../docs/project-workbook.md) and write one resource action:

```text
POST /api/tasks
Input: title, dueDate
Success: 201 + saved task + Location header
Failures: 400 invalid input, 401/403 access, 409 conflict
```

## 2. Generate the project

At [Spring Initializr](https://start.spring.io/), select Maven, Java, Jar, a supported Java version, and:

- Spring Web;
- Validation;
- Actuator.

Add [data storage](../capabilities/data-storage.md) or [security](../capabilities/security.md) only when required.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Continue only after the untouched application starts.

## 3. Build one endpoint end to end

For a database-backed API, follow the exact code order in the [core implementation guide](../docs/core-guide.md):

```text
contract → entity → repository → request/response DTOs
→ mapper → service → controller → error handler → request
```

Without a database, omit entity/repository and let the service call the required adapter.

## 4. Complete the API behavior

Add only required operations:

- create: `POST` → `201`;
- read/list: `GET` → `200`;
- update: `PUT` or `PATCH` → `200`;
- delete: `DELETE` → `204`;
- paginate every collection that can grow;
- return stable `ProblemDetail` errors.

## 5. Attach required capabilities

Add one at a time at the service boundary:

- [Data storage](../capabilities/data-storage.md)
- [Security](../capabilities/security.md)
- [External API](../capabilities/external-api.md)
- [Messaging](../capabilities/messaging.md)
- [File storage](../capabilities/file-storage.md)
- [Caching](../capabilities/caching.md)

## 6. Verify

- Service unit tests cover rules.
- MVC tests cover routes, JSON, validation, status, and errors.
- Persistence tests cover mappings and custom queries.
- One integration test covers the critical full flow.
- Manual requests prove the happy path and important failures.

```bash
./mvnw clean verify
```

## 7. Finish

Repeat from Step 1 for the next required endpoint. When requirements are complete, use the [production checklist](../docs/production-checklist.md).

Done means the API contract is documented, bounded, secured where required, tested, observable, and runnable in a clean environment.
