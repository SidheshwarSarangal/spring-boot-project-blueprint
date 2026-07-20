# Prepare for production

[← Application selector](../README.md) · [Troubleshooting](troubleshooting.md) · [Project workbook](project-workbook.md)

Complete this when the required features work. Production readiness means the same tested artifact can be safely configured and run in each environment.

## Database

- Use the production database engine in important integration tests, commonly through Testcontainers.
- Manage schema changes with Flyway or Liquibase; do not use `ddl-auto: update`.
- Add constraints and indexes that enforce real rules and access patterns.
- Configure pool limits, query timeouts, backups, and a tested restore procedure.
- Plan backward-compatible schema changes for rolling deployments.

## Configuration and secrets

- Keep normal defaults in `application.yml`.
- Supply environment-specific values through environment variables or the deployment platform.
- Group application settings with `@ConfigurationProperties` and validate required values at startup.
- Store passwords, tokens, and keys in a secret manager; never commit them.
- Build one JAR and promote that same artifact between environments.

Example:

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
```

## Security

- Authenticate every non-public entry point and authorize sensitive actions.
- Validate ownership in the service, not only at the URL layer.
- Restrict CORS to required origins and methods.
- Keep CSRF protection for browser session applications.
- Validate and bound every input, upload, page size, and request body.
- Update supported dependencies and review vulnerability reports.
- Never log credentials, tokens, session IDs, or sensitive payloads.

## Reliability

- Set timeouts on database and external network calls.
- Retry only transient, safe operations with a limit and backoff.
- Make externally retried write operations idempotent.
- Shut down gracefully so in-flight work can finish or return to a queue.
- Set CPU, memory, thread-pool, connection-pool, and queue limits.

## Observability

- Add Actuator and expose only the endpoints the platform needs.
- Provide liveness and readiness checks.
- Use structured logs with request/trace identifiers and useful context.
- Record metrics for request rate, latency, errors, pool usage, and important business outcomes.
- Alert on user-visible symptoms and exhausted resources, not every isolated error.

## Build and delivery

The pipeline should:

```text
checkout → compile → test → package → scan → publish artifact → migrate → deploy → health check
```

- Run `./mvnw clean verify` from a clean checkout.
- Pin the Java runtime and build reproducibly.
- Package the JAR or container without source secrets.
- Run migrations as a controlled deployment step.
- Use a rollback or roll-forward plan and verify health after deployment.

## Required project README

Document only what an operator or contributor needs:

```text
Purpose
Prerequisites
Configuration variables
Local database/setup
Run command
Test command
Example request
Migration command/process
Health endpoint
Known operational limitations
```

## Release gate

```bash
./mvnw clean verify
git status --short
```

- A clean environment can build and start the application.
- Migrations apply to an empty and an existing supported database.
- Required happy and failure paths pass.
- Protected operations reject unauthenticated and unauthorized access.
- No secret or generated local data is tracked.
- Health, logs, and metrics make failures diagnosable.
- Backup, restore, deployment, and rollback procedures are known.

Do not deploy until every applicable item is true or its risk is explicitly accepted.
