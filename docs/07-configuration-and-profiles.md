# 07 · Configuration and profiles

[← Validation](06-validation-and-errors.md) · [README](../README.md) · [Next: Testing →](08-testing.md)

## One artifact, many environments

```mermaid
flowchart TB
    Jar["Same application JAR"]
    Jar --> Local["local profile<br/>H2 + verbose logs"]
    Jar --> Test["test profile<br/>isolated dependencies"]
    Jar --> Prod["prod profile<br/>managed DB + restricted ops"]
```

Code should not be rebuilt just to change a database URL, port, timeout, or provider key.

## Configuration sources

```mermaid
flowchart LR
    Defaults["application.yml"] --> Profile["application-prod.yml"]
    Profile --> Env["Environment variables"]
    Env --> CLI["Command-line arguments"]
    CLI --> Effective["Effective configuration"]
```

Later/higher-precedence sources override earlier values. Spring Boot supports properties/YAML, environment variables, command-line arguments, and more.

## Base configuration

```yaml
spring:
  application:
    name: taskboard-api
  datasource:
    url: jdbc:h2:file:./data/taskboard
  jpa:
    hibernate:
      ddl-auto: update

management:
  endpoints:
    web:
      exposure:
        include: health,info
```

## Environment override

```bash
export SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5432/taskboard'
export SPRING_DATASOURCE_USERNAME='taskboard'
export SPRING_DATASOURCE_PASSWORD='change-me'
export SPRING_PROFILES_ACTIVE='prod'
```

```mermaid
flowchart LR
    Key["spring.datasource.url"] --> Rule["dots → underscores<br/>remove dashes<br/>uppercase"]
    Rule --> Env["SPRING_DATASOURCE_URL"]
```

## Group custom settings

Prefer typed settings over scattered `@Value` strings:

```java
@ConfigurationProperties("app.notifications")
@Validated
public record NotificationProperties(
    @NotBlank String sender,
    @NotNull Duration timeout
) {}
```

```mermaid
flowchart LR
    YAML["app.notifications.*"] --> Binder["Boot binder"]
    Binder --> Validate["Validate settings"]
    Validate --> Bean["NotificationProperties bean"]
    Bean --> Service["Notification service"]
```

Invalid required configuration should fail at startup, not during the first customer request.

## Profiles

```mermaid
flowchart TD
    Base["application.yml"] --> Active{"Active profile"}
    Active -->|"local"| Local["application-local.yml"]
    Active -->|"test"| Test["application-test.yml"]
    Active -->|"prod"| Prod["application-prod.yml"]
```

Use profiles for groups of environment-specific settings or beans. Avoid dozens of fine-grained profiles that create untestable combinations.

## Secret flow

```mermaid
flowchart LR
    Manager["Secret manager / platform"] --> Env["Runtime environment"]
    Env --> App["Spring configuration"]
    App --> Client["Database / provider client"]
    Git["Git repository"] -. "never store secrets" .-> Manager
```

Never commit passwords, API keys, private keys, production JWT secrets, or cloud credentials.

## Configuration checklist

- [ ] Defaults are safe for local development.
- [ ] Production values come from the deployment environment.
- [ ] Secrets are absent from Git and logs.
- [ ] Related custom settings use `@ConfigurationProperties`.
- [ ] Required settings are validated at startup.
- [ ] Production schema handling uses migrations rather than `ddl-auto: update`.
- [ ] Only safe Actuator endpoints are exposed publicly.
