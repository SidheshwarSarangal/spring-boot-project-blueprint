# Path: API gateway or proxy

[← Choose another type](../README.md) · [Troubleshooting](../docs/troubleshooting.md)

Choose this when the application is the controlled entry point to downstream services. Do not place ordinary business workflows in the gateway.

## 1. Define the routing contract

Record incoming route, downstream target, path/header changes, authentication, rate limit, timeout, retry rule, and failure response in the [project workbook](../docs/project-workbook.md).

## 2. Generate the project

Select Spring Cloud Gateway and Actuator. Add the security mechanism required by the organization. Keep dependencies minimal; a gateway normally does not own business entities or a database.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Continue only after the untouched gateway starts.

## 3. Build one route

```text
client → gateway filter chain → route/policy → downstream service
```

1. Configure an explicit route and allowed methods.
2. Authenticate and propagate only approved identity/context headers.
3. Remove untrusted client headers that could impersonate internal context.
4. Set connection/response timeouts and request/body limits.
5. Add rate limiting where abuse or capacity requires it.
6. Retry only safe requests and transient failures.
7. Keep errors predictable without exposing internal details.

## 4. Attach required capabilities

- [Security](../capabilities/security.md)
- [External API](../capabilities/external-api.md) concepts for downstream timeout/retry safety
- [Caching](../capabilities/caching.md) only for explicitly safe responses

## 5. Verify

Test routing, unknown route, authentication, header stripping/propagation, timeout, downstream outage, body limit, rate limit, and retry safety. Verify that correlation/trace identifiers continue downstream.

```bash
./mvnw clean verify
```

## 6. Finish

Document route ownership and dependencies, then complete the [production checklist](../docs/production-checklist.md).

Done means routing and policies are explicit, unsafe headers cannot bypass trust boundaries, failures are bounded, and the gateway contains no hidden business workflow.
