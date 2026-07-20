# Capability process: Add an external HTTP API

[← Application selector](../README.md) · [Integration process](../paths/integration-service.md) · [Configuration](../docs/configuration-guide.md)

Insert this process when a use case calls payments, email, maps, AI, identity, or another HTTP provider.

> ↩ Keep the application path open. After Step 5, return to the exact application step that sent you here, finish its check, and continue from there.

## Step 1 · Define the provider boundary

> 📍 Add an `External provider` section under the current feature in `<project-root>/PROJECT.md`.

Record operation, application input/output, credential source, connect/response timeout, rate limit, retry safety, idempotency support, and outage behavior.

Before continuing, check: Service behavior is stated without exposing provider DTO/status details.

Continue to Step 2.

## Step 2 · Create interface, DTOs, properties, and adapter

> 📍 Create these paths; replace `com/company/project` with the package selected in Initializr.

```text
src/main/java/com/company/project/provider/
├── ProviderClient.java
├── HttpProviderAdapter.java
├── ProviderRequest.java
├── ProviderResponse.java
├── ProviderConfiguration.java
└── ProviderProperties.java
```

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

Before continuing, check: Service compiles against `ProviderClient`; provider DTOs remain inside adapter package.

Continue to Step 3.

## Step 3 · Configure a bounded HTTP client

> 📍 Edit `src/main/java/com/company/project/provider/ProviderConfiguration.java`, `ProviderProperties.java`, and `src/main/resources/application.yml`.

For a synchronous MVC application use `RestClient`; use `WebClient` for selected reactive/streaming flows.

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

Before continuing, check: Missing/invalid properties fail startup and a stubbed delayed response stops within configured bound.

Continue to Step 4.

## Step 4 · Implement and translate the call

> 📍 Edit `src/main/java/com/company/project/provider/HttpProviderAdapter.java`, `ProviderRequest.java`, and `ProviderResponse.java`.

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

Before continuing, check: Stub success maps correctly; each expected provider failure maps to the contract without leaking provider secrets.

Continue to Step 5.

## Step 5 · Test offline and observe

> 📍 Create `src/test/java/com/company/project/provider/HttpProviderAdapterTest.java`; configure safe logging/metrics in `src/main/resources/application.yml`; run tests in `<project-root>/` and the same command in CI.

Test success, malformed/empty response, timeout, connection failure, auth failure, rate limit, retry exhaustion, duplicate, and outage. Record safe correlation/provider ID, latency, and outcome—not credentials/payload secrets.

```bash
./mvnw clean verify
```

Before continuing, check: Normal automated tests run offline; calls cannot block forever; duplicate side effects are controlled.

Return to the application step that sent you here, finish that step’s remaining instructions and check, then continue from its stated next step.
