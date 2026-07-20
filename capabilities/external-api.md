# Capability: External HTTP API

[← Application selector](../README.md) · [Integration-service path](../paths/integration-service.md)

Add this when a use case calls payments, email, maps, AI, identity, or another HTTP provider.

## 1. Define the boundary

Record provider operation, required input/output, credential source, timeout, rate limit, retry safety, idempotency support, and outage behavior.

## 2. Isolate the provider

```text
service → application-owned interface → provider adapter
→ configured HTTP client → provider
```

Use your own application types at the interface. Keep provider DTOs, headers, status codes, and error formats inside the adapter.

For a normal synchronous Spring MVC service, begin with `RestClient`; use `WebClient` when the selected application is reactive/streaming. Configure the client once as a bean and inject it into the adapter:

```java
@Bean
RestClient providerRestClient(RestClient.Builder builder,
                              ProviderProperties properties) {
    return builder.baseUrl(properties.baseUrl().toString()).build();
}
```

Configure connection/response timeouts on the underlying request factory/client; a base URL alone is not production-safe.

```text
src/main/java/com/company/project/provider/
├── ProviderClient.java          application-owned interface
├── HttpProviderAdapter.java
├── ProviderRequest.java        provider-only DTO
├── ProviderResponse.java       provider-only DTO
├── ProviderConfiguration.java
└── ProviderProperties.java
src/test/java/com/company/project/provider/HttpProviderAdapterTest.java
```

## 3. Configure safety

1. Externalize URL, credentials, connection timeout, response timeout, and limits.
2. Validate provider responses before using them.
3. Retry only transient failures and safe/idempotent operations.
4. Use bounded exponential backoff.
5. Use an idempotency key for retryable side effects where supported.
6. Respect rate-limit responses; do not retry immediately in a loop.
7. Map provider errors to stable application errors without leaking provider details.

Checkpoint: call a local stub for one success and one timeout. Do not connect the real provider until the adapter contract tests pass.

## 4. Observe

Record request correlation/provider IDs, latency, outcome, and rate-limit state without recording secrets or sensitive payloads.

## 5. Verify offline

Use a stub HTTP server. Test success, invalid response, timeout, connection failure, authentication failure, rate limit, retry exhaustion, duplicate request, and provider outage.

Completion: normal tests do not call the real provider, failures cannot block forever, retries cannot casually duplicate side effects, and changing providers is isolated to adapters.
