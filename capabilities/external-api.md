# Capability process: Add an external HTTP API

[← Application selector](../README.md) · [Integration process](../paths/integration-service.md) · [Configuration](../docs/configuration-guide.md)

Insert this process when a use case calls payments, email, maps, AI, identity, or another HTTP provider.

## Step 1 · Define the provider boundary

**What:** Specify application-owned input/output and provider failure rules.

**Where:** Feature sheet in `PROJECT.md`.

**Do:** Record operation, application input/output, credential source, connect/response timeout, rate limit, retry safety, idempotency support, and outage behavior.

**Verify:** Service behavior is stated without exposing provider DTO/status details.

**Next:** Step 2.

## Step 2 · Create interface, DTOs, properties, and adapter

**What:** Isolate provider code behind an application interface.

**Where:**

```text
src/main/java/com/company/project/provider/
├── ProviderClient.java
├── HttpProviderAdapter.java
├── ProviderRequest.java
├── ProviderResponse.java
├── ProviderConfiguration.java
└── ProviderProperties.java
```

**Do:**

```java
public interface ProviderClient {
    ProviderResult perform(ProviderCommand command);
}

@Validated
@ConfigurationProperties("provider")
public record ProviderProperties(
    @NotNull URI baseUrl,
    @NotBlank String apiKey,
    @NotNull Duration timeout
) {}
```

**Verify:** Service compiles against `ProviderClient`; provider DTOs remain inside adapter package.

**Next:** Step 3.

## Step 3 · Configure a bounded HTTP client

**What:** Create one reusable client with externalized URL/credentials/timeouts.

**Where:** `ProviderConfiguration.java`, `application.yml`, underlying request-factory/client configuration.

**Do:** For a synchronous MVC application use `RestClient`; use `WebClient` for selected reactive/streaming flows.

```java
@Bean
RestClient providerRestClient(RestClient.Builder builder,
                              ProviderProperties properties) {
    HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(properties.timeout())
        .build();
    JdkClientHttpRequestFactory requestFactory =
        new JdkClientHttpRequestFactory(httpClient);
    requestFactory.setReadTimeout(properties.timeout());
    return builder
        .requestFactory(requestFactory)
        .baseUrl(properties.baseUrl().toString())
        .defaultHeader("Authorization", "Bearer " + properties.apiKey())
        .build();
}
```

```yaml
provider:
  base-url: ${PROVIDER_BASE_URL}
  api-key: ${PROVIDER_API_KEY}
  timeout: 3s
```

Use separate connect/response durations when the requirement needs different bounds.

**Verify:** Missing/invalid properties fail startup and a stubbed delayed response stops within configured bound.

**Next:** Step 4.

## Step 4 · Implement and translate the call

**What:** Convert application command → provider request → application result.

**Where:** `HttpProviderAdapter.java`.

**Do:**

```java
@Component
class HttpProviderAdapter implements ProviderClient {
    private final RestClient client;

    HttpProviderAdapter(RestClient client) {
        this.client = client;
    }

    public ProviderResult perform(ProviderCommand command) {
        ProviderResponse response = client.post()
            .uri("/operations")
            .body(ProviderRequest.from(command))
            .retrieve()
            .body(ProviderResponse.class);
        if (response == null) throw new ProviderUnavailableException();
        return response.toResult();
    }
}
```

Translate decline/invalid/rate-limit/auth/outage to stable application outcomes. Retry only transient safe/idempotent operations with limit/backoff; use provider idempotency keys for side effects.

**Verify:** Stub success maps correctly; each expected provider failure maps to the contract without leaking provider secrets.

**Next:** Step 5.

## Step 5 · Test offline and observe

**What:** Prove provider behavior without real network dependency.

**Where:** Stub-server adapter tests, metrics/logging, CI.

**Do:** Test success, malformed/empty response, timeout, connection failure, auth failure, rate limit, retry exhaustion, duplicate, and outage. Record safe correlation/provider ID, latency, and outcome—not credentials/payload secrets.

```bash
./mvnw clean verify
```

**Verify:** Normal automated tests run offline; calls cannot block forever; duplicate side effects are controlled.

**Next:** Return to the application path’s next step.
