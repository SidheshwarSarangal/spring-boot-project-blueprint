# Configuration and environments

[← Application selector](../README.md) · [Production checklist](production-checklist.md) · [Troubleshooting](troubleshooting.md)

Build one application artifact and change behavior between environments through configuration—not source edits.

## 1. Put normal defaults in the base file

Use `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: orders-api

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info
```

Keep the file small. Use Spring Boot’s documented property names rather than creating duplicates.

## 2. Externalize environment values

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}

provider:
  base-url: ${PROVIDER_BASE_URL}
  api-key: ${PROVIDER_API_KEY}
  timeout: 3s
```

`${NAME}` requires a value. `${NAME:default}` supplies a safe non-secret default.

## 3. Group application-owned settings

```java
@Validated
@ConfigurationProperties(prefix = "provider")
public record ProviderProperties(
    @NotNull URI baseUrl,
    @NotBlank String apiKey,
    @NotNull Duration timeout
) {}
```

Enable configuration-property scanning in the application/configuration package. Inject this record into the adapter/configuration that needs it. Validated configuration makes startup fail early instead of failing during a request.

## 4. Use profiles narrowly

Profiles are useful for grouped environment differences, not secret storage.

```text
application.yml          shared defaults
application-local.yml    disposable local services/logging
application-test.yml     automated-test overrides
```

Activate deliberately:

```bash
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

Avoid a profile for every developer or scattered `@Profile` annotations that change business behavior.

## 5. Keep secrets outside Git

Use environment/deployment secret storage. Do not commit `.env`, real tokens, passwords, keys, certificates, or production URLs containing credentials. Provide variable names and examples without values in the project README.

## 6. Configure external resources explicitly

For every database, HTTP provider, broker, cache, or file store document:

- URL/host and credential source;
- connection and operation timeouts;
- pool/concurrency limits;
- retry behavior;
- local development replacement;
- health behavior when it is unavailable.

## 7. Verify each environment

```bash
./mvnw clean verify
java -jar target/<application>.jar
```

Check startup with valid settings, startup failure for missing/invalid required settings, secret absence from logs/error responses, and health behavior when dependencies are available/unavailable.

Completion: one built JAR runs in local/test/production configuration without source changes, required settings fail fast, and all secrets remain external.
