# Process: Build an API gateway or proxy

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this when the application is a controlled entry point to downstream services. Keep ordinary business workflows out of the gateway.

## Repository action map

| Step | Exact location | Add or run there |
|---|---|---|
| 1 | Create `<project-root>/PROJECT.md` | Route/trust/limit contract |
| 2 | Browser: Initializr; Terminal: generated `<project-root>` | Generate/build/start gateway |
| 3 | Edit `src/main/resources/application.yml`; run local stub separately | Route YAML and route request |
| 4 | Create `src/main/java/com/company/gateway/CorrelationIdFilter.java`; edit security/route config | Trust, limit, timeout code |
| 5 | Create `src/test/java/com/company/gateway/`; edit config/CI/route docs | Gateway tests and delivery |

**Beginner actions by step:** 1 → [A workbook](../docs/beginner-execution-guide.md#action-a-create-the-working-repository-and-workbook); 2 → [B generate](../docs/beginner-execution-guide.md#action-b-generate-the-spring-project-in-the-browser), [D terminal](../docs/beginner-execution-guide.md#action-d-run-a-command-in-the-correct-terminal); 3 → [H route YAML](../docs/beginner-execution-guide.md#action-h-edit-yaml-configuration), [J call](../docs/beginner-execution-guide.md#action-j-start-the-application-and-call-it); 4 → [E create filter](../docs/beginner-execution-guide.md#action-e-create-a-java-package-and-file), [F add code](../docs/beginner-execution-guide.md#action-f-put-a-provided-java-code-block-into-a-file); 5 → [K tests](../docs/beginner-execution-guide.md#action-k-create-and-run-a-test), [M checkpoint](../docs/beginner-execution-guide.md#action-m-save-a-clean-checkpoint-with-git).

## Step 1 · Define one route and policy

**What:** Produce an incoming → downstream routing contract.

**Where:** One feature sheet in `PROJECT.md`.

**Do:** Record method/path, downstream URI, path rewrite, allowed/removed headers, authentication, rate/body limits, timeout, retry safety, and failure response.

**Verify:** Trust boundaries and downstream ownership are explicit.

**Next:** Step 2.

## Step 2 · Generate and run gateway foundation

**What:** Start a gateway without business persistence.

**Where:** Spring Initializr and project root.

**Do:** Select Spring Cloud Gateway Server WebFlux and Actuator for the examples below; add security when required. If the organization selects the Web MVC variant, use its variant-specific configuration/API from the official reference. Do not add JPA/entities without a gateway-owned requirement.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

**Verify:** Gateway starts and health returns `UP` before routes are added.

**Next:** Step 3.

## Step 3 · Configure one route

**What:** Route a bounded request to a local downstream stub.

**Where:** `src/main/resources/application.yml`.

**Do:** Server WebFlux route:

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

**Verify:** Allowed request reaches the stub with intended downstream path; unmatched route returns the intended not-found response.

**Next:** Step 4.

## Step 4 · Add trust, limits, and failure controls

**What:** Enforce cross-cutting policies without hiding business logic.

**Where:** Security configuration, gateway filters, route properties, rate limiter, observability configuration.

**Do:** Authenticate; remove spoofable identity headers; add only approved internal identity context; set connection/response/body limits; rate-limit where required; retry only idempotent safe requests; propagate correlation/trace ID.

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

**Verify:** Spoofed internal header is removed/replaced; timeout/outage is bounded; rate/body limits reject excess input; correlation ID reaches stub.

**Next:** Step 5.

## Step 5 · Test and deliver

**What:** Prove every route/policy and deliver the gateway.

**Where:** Gateway tests, configuration, CI/deployment, route-owner documentation.

**Do:** Test routing, unknown route, auth, header stripping/propagation, rewrite, timeout, outage, body limit, rate limit, and retry safety. Add only [security](../capabilities/security.md), downstream [external-call safety](../capabilities/external-api.md), or carefully measured [caching](../capabilities/caching.md).

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

**Verify:** Clean build passes; all routes have owners/policies; clean deployment routes and fails safely.

**Next:** Release, or return to Step 1 for another route.
