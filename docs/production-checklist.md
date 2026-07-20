# Prepare for production

[← Application selector](../README.md) · [Troubleshooting](troubleshooting.md) · [Project workbook](project-workbook.md)

Complete this when the required features work. Production readiness means the same tested artifact can be safely configured and run in each environment.

Use the [delivery guide](delivery-guide.md) for the concrete package → CI → migrate → deploy sequence, then use this page as the release gate.

## Database

> 📍 Review `pom.xml`, `src/main/resources/db/migration/`, database integration tests under `src/test/java/`, and `<project-root>/docs/database-runbook.md`.

- Use the production database engine in important integration tests, commonly through Testcontainers.
- Manage schema changes with Flyway or Liquibase; do not use `ddl-auto: update`.
- Add constraints and indexes that enforce real rules and access patterns.
- Configure pool limits, query timeouts, backups, and a tested restore procedure.
- Plan backward-compatible schema changes for rolling deployments.

## Configuration and secrets

> 📍 Review `src/main/resources/application*.yml`, typed configuration under `src/main/java/`, and the deployment platform’s environment/secret settings.

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

> 📍 Review the security configuration and feature services under `src/main/java/`, security tests under `src/test/java/`, and deployed CORS/identity-provider settings.

- Authenticate every non-public entry point and authorize sensitive actions.
- Validate ownership in the service, not only at the URL layer.
- Restrict CORS to required origins and methods.
- Keep CSRF protection for browser session applications.
- Validate and bound every input, upload, page size, and request body.
- Update supported dependencies and review vulnerability reports.
- Never log credentials, tokens, session IDs, or sensitive payloads.

## Reliability

> 📍 Review external adapters, job/listener configuration, `application.yml`, and the deployment platform’s CPU, memory, concurrency, and shutdown settings.

- Set timeouts on database and external network calls.
- Retry only transient, safe operations with a limit and backoff.
- Make externally retried write operations idempotent.
- Shut down gracefully so in-flight work can finish or return to a queue.
- Set CPU, memory, thread-pool, connection-pool, and queue limits.

## Observability

> 📍 Review Actuator settings in `src/main/resources/application.yml`, application logging/metrics code, and the deployment monitoring platform.

- Add Actuator and expose only the endpoints the platform needs.
- Provide liveness and readiness checks.
- Use structured logs with request/trace identifiers and useful context.
- Record metrics for request rate, latency, errors, pool usage, and important business outcomes.
- Alert on user-visible symptoms and exhausted resources, not every isolated error.

## Build and delivery

> 📍 Review the CI workflow, `pom.xml`, deployment files, and runbooks in `<project-root>/docs/`.

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

> 📍 Edit `<project-root>/README.md`; link detailed procedures from `<project-root>/docs/` instead of duplicating them.

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

> 📍 Run these commands in `<project-root>/`, then verify the deployed application from a terminal that can reach it.

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
