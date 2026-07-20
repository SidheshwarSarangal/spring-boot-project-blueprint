# Process: Build an API gateway or proxy

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this when the application is a controlled entry point to downstream services. Keep ordinary business workflows out of the gateway.

## Step 1 · Define one route and policy

Create or edit `<project-root>/PROJECT.md`, section **5. Feature sheet**.

Record method/path, downstream URI, path rewrite, allowed/removed headers, authentication, rate/body limits, timeout, retry safety, and failure response.

Before continuing, check: Trust boundaries and downstream ownership are explicit.

Continue to Step 2.

## Step 2 · Generate and run gateway foundation

Open [Spring Initializr](https://start.spring.io/) in the browser. After downloading and extracting the project, open a terminal in `<project-root>/`, the folder containing `pom.xml` and `mvnw`.

Select Spring Cloud Gateway Server WebFlux and Actuator for the examples below; add security when required. If the organization selects the Web MVC variant, use its variant-specific configuration/API from the official reference. Do not add JPA/entities without a gateway-owned requirement.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Before continuing, check: Gateway starts and health returns `UP` before routes are added.

Continue to Step 3.

## Step 3 · Configure one route

Edit `<project-root>/src/main/resources/application.yml`; run the gateway in a terminal at `<project-root>` and the downstream stub in a second terminal.

Server WebFlux route:

```yaml
spring:
  cloud:
    gateway:
      server:
        webflux:
          routes:
            - id: orders
              uri: http://localhost:9090
              predicates:
                - Path=/api/orders/**
              filters:
                - StripPrefix=1
```

Start a local stub on the target URI.

Before continuing, check: Allowed request reaches the stub with intended downstream path; unmatched route returns the intended not-found response.

Continue to Step 4.

## Step 4 · Add trust, limits, and failure controls

Create `src/main/java/com/company/gateway/CorrelationIdFilter.java` and `src/main/java/com/company/gateway/security/SecurityConfiguration.java`. Edit `src/main/resources/application.yml` for timeout, body-size, and rate settings.

Authenticate; remove spoofable identity headers; add only approved internal identity context; set connection/response/body limits; rate-limit where required; retry only idempotent safe requests; propagate correlation/trace ID.

```java
@Component
class CorrelationIdFilter implements GlobalFilter {
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String id = Optional.ofNullable(
            exchange.getRequest().getHeaders().getFirst("X-Correlation-Id")
        ).orElseGet(() -> UUID.randomUUID().toString());
        ServerHttpRequest request = exchange.getRequest().mutate()
            .headers(headers -> headers.set("X-Correlation-Id", id))
            .build();
        return chain.filter(exchange.mutate().request(request).build());
    }
}
```

Before continuing, check: Spoofed internal header is removed/replaced; timeout/outage is bounded; rate/body limits reject excess input; correlation ID reaches stub.

Continue to Step 5.

## Step 5 · Test and deliver

Create tests under `src/test/java/com/company/gateway/`. Edit `src/main/resources/application.yml`, the CI/deployment files in `<project-root>/`, and `<project-root>/README.md`.

Test routing, unknown route, auth, header stripping/propagation, rewrite, timeout, outage, body limit, rate limit, and retry safety. Add only [security](../capabilities/security.md), downstream [external-call safety](../capabilities/external-api.md), or carefully measured [caching](../capabilities/caching.md).

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

Before continuing, check: Clean build passes; all routes have owners/policies; clean deployment routes and fails safely.

Release, or return to Step 1 for another route.
