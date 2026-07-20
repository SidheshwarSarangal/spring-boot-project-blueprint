# Package and deliver the application

[← Application selector](../README.md) · [Configuration](configuration-guide.md) · [Production checklist](production-checklist.md)

Use this after required features pass. Choose JAR or container delivery according to the target platform; do not create both unless needed.

Use [Action D](beginner-execution-guide.md#action-d-run-a-command-in-the-correct-terminal) for commands, [Action H](beginner-execution-guide.md#action-h-edit-yaml-configuration) for configuration, and [Action M](beginner-execution-guide.md#action-m-save-a-clean-checkpoint-with-git) before publishing.

## 1. Produce the verified artifact

```bash
./mvnw clean verify
```

The executable JAR appears under `target/`. Run the exact file:

```bash
java -jar target/orders-api-0.0.1-SNAPSHOT.jar
```

Call the health endpoint and one safe use case. The JAR that passed tests should be the same artifact promoted to later environments.

## 2. Run with production-like configuration

Set required variables outside source control:

```bash
export DATABASE_URL='jdbc:postgresql://localhost:5432/orders'
export DATABASE_USERNAME='orders_app'
export DATABASE_PASSWORD='local-only-value'
java -jar target/orders-api-0.0.1-SNAPSHOT.jar
```

Use a local secret value only for local verification. Confirm missing/invalid required configuration fails during startup.

## 3. Containerize only when the platform uses containers

Build the JAR first. Example `Dockerfile` (replace the Java version and artifact name to match the project):

```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/orders-api-0.0.1-SNAPSHOT.jar app.jar
USER 10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

```bash
docker build -t orders-api:local .
docker run --rm -p 8080:8080 \
  -e DATABASE_URL \
  -e DATABASE_USERNAME \
  -e DATABASE_PASSWORD \
  orders-api:local
```

Do not copy `.env`, source secrets, local databases, or build caches into the image. Run as a non-root user and use a small supported runtime image.

## 4. Create the CI gate

Every pushed change should run from a clean checkout with the project wrapper:

```text
checkout
→ select supported JDK
→ ./mvnw clean verify
→ dependency/security scan
→ publish immutable JAR or image
```

Pin the JDK major version and CI actions/plugins. Keep deployment credentials in the CI/platform secret store. Publish artifacts only after tests pass.

## 5. Plan database migration order

For a database-backed application:

1. Back up or confirm recovery capability.
2. Apply tested forward-compatible Flyway/Liquibase migrations.
3. Deploy code compatible with both transition and final schema when rolling instances.
4. Verify migration history and application readiness.
5. Remove old columns/behavior only in a later release after old code is gone.

Never repair production by deleting its database or editing an already-applied migration file.

## 6. Deploy and verify

```text
publish immutable artifact
→ supply configuration/secrets
→ apply migrations
→ start application
→ readiness succeeds
→ smoke test
→ monitor errors/latency/resources
```

Smoke-test one safe happy path and one authentication/permission boundary where relevant. Verify logs contain correlation context but no secrets.

## 7. Roll back or roll forward

Before deployment decide:

- how to stop new traffic;
- which prior artifact is recoverable;
- whether schema changes permit old code;
- whether failed messages/jobs need replay;
- who decides and performs recovery.

Prefer a tested roll-forward when a database migration cannot safely be reversed. Never assume replacing the JAR also reverses data changes.

## 8. Write the operator handoff

The project README must include:

```text
required Java/runtime
configuration variable names
build and test command
local run command
migration process
artifact/container run command
health/readiness endpoint
smoke-test command
known dependencies and limits
rollback/roll-forward owner
```

## Delivery completion gate

- Clean checkout produces the artifact.
- The artifact starts with external configuration and no source edits.
- CI runs the clean verification command.
- Migrations are tested and ordered safely.
- Readiness and smoke tests pass after deployment.
- Secrets are absent from Git, artifact, image, logs, and responses.
- Recovery is documented and assigned.

Finish with the [production checklist](production-checklist.md).
