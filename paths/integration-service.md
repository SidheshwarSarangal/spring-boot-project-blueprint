# Path: External-system integration service

[← Choose another type](../README.md) · [External API capability](../capabilities/external-api.md) · [Troubleshooting](../docs/troubleshooting.md)

Choose this when the main purpose is coordinating payments, email, maps, AI, identity, or another provider.

## 1. Define the integration action

```text
Caller/trigger:
Provider operation:
Application input/output:
Credential source:
Timeout and rate limit:
Retry/idempotency rule:
Provider outage behavior:
```

Record it in the [project workbook](../docs/project-workbook.md).

## 2. Choose the entry point

- HTTP caller: begin with the [REST API path](rest-api.md).
- Message-triggered integration: begin with the [event-driven path](event-driven-service.md).
- Timed synchronization: begin with the [background worker path](background-worker.md).

Then add the [External API capability](../capabilities/external-api.md).

## 3. Build one integration flow

```text
entry point → service → application-owned provider interface
→ provider adapter → HTTP client → external system
```

1. Keep provider DTOs and errors inside the adapter.
2. Externalize URLs, credentials, timeouts, and limits.
3. Validate provider responses before using them.
4. Retry only transient, safe operations with a limit/backoff.
5. Use idempotency keys for side-effecting operations where supported.
6. Record correlation/provider IDs needed for support without logging secrets.

## 4. Attach required capabilities

- [Security](../capabilities/security.md)
- [Data storage](../capabilities/data-storage.md) for state/audit/idempotency
- [Messaging](../capabilities/messaging.md) for durable asynchronous work
- [Caching](../capabilities/caching.md) only for safe, measured repeated reads

## 5. Verify

Use a stub server to test success, malformed response, validation rejection, timeout, rate limit, authentication failure, retry exhaustion, and provider outage. Do not make ordinary automated tests depend on the real provider.

```bash
./mvnw clean verify
```

## 6. Finish

Document provider configuration and operational limits, then complete the [production checklist](../docs/production-checklist.md).

Done means provider changes are isolated, failures cannot block forever, duplicate side effects are controlled, and the service is testable offline.
