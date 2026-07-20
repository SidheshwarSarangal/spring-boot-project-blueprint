# Process: Build an external-system integration service

[← Choose another type](../README.md) · [Working starter](../starters/integration-service/README.md) · [External API](../capabilities/external-api.md) · [Testing](../docs/testing-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this when the main purpose is coordinating a payment, email, maps, AI, identity, or another provider.

## Step 1 · Define one integration action

> 📍 Create or edit `<project-root>/PROJECT.md`, section **5. Feature sheet**.

Record caller/trigger, application input/output, provider operation, credential source, timeout, rate limit, retry safety, idempotency, and outage behavior.

Before continuing, check: The application result remains understandable without provider-specific field names.

Continue to Step 2.

## Step 2 · Choose entry point and generate foundation

> 📍 Work in the files/terminal named by the chosen [REST](rest-api.md), [event](event-driven-service.md), or [background](background-worker.md) entry path; return here after its foundation step passes.

Generate that path’s minimum dependencies plus Actuator. Do not add a second web/reactive stack without a requirement.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Before continuing, check: Entry-point foundation starts before provider code exists.

Continue to Step 3.

## Step 3 · Create provider boundary and adapter

> 📍 Under `src/main/java/com/company/project/`, create the `payment/` folder and these files. Replace `com/company/project` with the package selected in Initializr.

```text
src/main/java/com/company/project/payment/
├── PaymentService.java
├── PaymentProvider.java
├── ProviderPaymentAdapter.java
├── ProviderRequest.java
├── ProviderResponse.java
├── ProviderConfiguration.java
└── ProviderProperties.java
```

Define an application-owned interface and adapter:

```java
public interface PaymentProvider {
    PaymentResult charge(PaymentCommand command);
}

@Component
class ProviderPaymentAdapter implements PaymentProvider {
    private final RestClient client;

    ProviderPaymentAdapter(RestClient client) {
        this.client = client;
    }

    public PaymentResult charge(PaymentCommand command) {
        ProviderResponse response = client.post()
            .uri("/charges")
            .body(ProviderRequest.from(command))
            .retrieve()
            .body(ProviderResponse.class);
        return response.toResult();
    }
}
```

Follow the [external API capability](../capabilities/external-api.md) for client configuration/timeouts.

Before continuing, check: `./mvnw compile` passes and service depends on `PaymentProvider`, not the concrete adapter/provider DTO.

Continue to Step 4.

## Step 4 · Handle provider outcomes safely

> 📍 Edit `src/main/java/com/company/project/payment/ProviderConfiguration.java`, `ProviderPaymentAdapter.java`, and `PaymentService.java`. Create or edit `src/main/java/com/company/project/common/error/ApiExceptionHandler.java`.

Configure connection/response timeouts; translate success/decline/rate-limit/auth/outage; retry only transient safe requests; use idempotency keys for retryable side effects; record safe correlation/provider IDs.

Before continuing, check: A stubbed timeout finishes within the configured bound and a decline becomes the intended application result—not a generic `500`.

Continue to Step 5.

## Step 5 · Add required durability/security

> 📍 Create only the linked required folder under `src/main/java/com/company/project/`: `security/`, feature persistence files, `messaging/`, or `cache/`. Edit `src/main/java/com/company/project/payment/PaymentService.java` to use it.

Choose [security](../capabilities/security.md), [data storage](../capabilities/data-storage.md), [messaging](../capabilities/messaging.md), or [caching](../capabilities/caching.md). Persist audit/idempotency state when required; never store/log provider credentials or full sensitive payloads.

Before continuing, check: Duplicate requests, cross-user access, and provider failure cannot produce an uncontrolled side effect or secret leak.

Continue to Step 6.

## Step 6 · Test and deliver

> 📍 Create tests under `src/test/java/com/company/project/payment/`. Edit `src/main/resources/application.yml`, the CI/deployment files in `<project-root>/`, and `<project-root>/README.md`. Run commands in `<project-root>/`.

Test success, malformed response, validation rejection, timeout, rate limit, authentication failure, retry exhaustion, duplicate request, and outage using the [testing guide](../docs/testing-guide.md).

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

Before continuing, check: Tests run offline; failures cannot block forever; duplicate effects are controlled; a clean environment can deploy the adapter settings externally.

Release, or return to Step 1 for another provider operation.
