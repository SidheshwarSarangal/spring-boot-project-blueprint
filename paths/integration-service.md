# Process: Build an external-system integration service

[← Choose another type](../README.md) · [External API](../capabilities/external-api.md) · [Testing](../docs/testing-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this when the main purpose is coordinating a payment, email, maps, AI, identity, or another provider.

## Step 1 · Define one integration action

**What:** Specify application behavior independently from the provider API.

**Where:** Create or edit `<project-root>/PROJECT.md`, section **5. Feature sheet**.

**Do now:** Record caller/trigger, application input/output, provider operation, credential source, timeout, rate limit, retry safety, idempotency, and outage behavior.

**Finish this step when:** The application result remains understandable without provider-specific field names.

**Go next:** Step 2.

## Step 2 · Choose entry point and generate foundation

**What:** Select how work starts and produce a running project.

**Where:** Work in the files/terminal named by the chosen [REST](rest-api.md), [event](event-driven-service.md), or [background](background-worker.md) entry path; return here after its foundation step passes.

**Do now:** Generate that path’s minimum dependencies plus Actuator. Do not add a second web/reactive stack without a requirement.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

**Finish this step when:** Entry-point foundation starts before provider code exists.

**Go next:** Step 3.

## Step 3 · Create provider boundary and adapter

**What:** Isolate provider-specific HTTP behavior from the service.

**Where:**

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

**Do now:** Define an application-owned interface and adapter:

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

**Finish this step when:** `./mvnw compile` passes and service depends on `PaymentProvider`, not the concrete adapter/provider DTO.

**Go next:** Step 4.

## Step 4 · Handle provider outcomes safely

**What:** Bound calls and translate every expected provider outcome.

**Where:** Edit `payment/ProviderConfiguration.java`, `payment/ProviderPaymentAdapter.java`, `payment/PaymentService.java`, and `common/error/ApiExceptionHandler.java`.

**Do now:** Configure connection/response timeouts; translate success/decline/rate-limit/auth/outage; retry only transient safe requests; use idempotency keys for retryable side effects; record safe correlation/provider IDs.

**Finish this step when:** A stubbed timeout finishes within the configured bound and a decline becomes the intended application result—not a generic `500`.

**Go next:** Step 5.

## Step 5 · Add required durability/security

**What:** Attach only state, security, asynchronous work, or caching required by the contract.

**Where:** Create only the linked required folder under `src/main/java/com/company/project/`: `security/`, feature persistence files, `messaging/`, or `cache/`; edit `payment/PaymentService.java` to use it.

**Do now:** Choose [security](../capabilities/security.md), [data storage](../capabilities/data-storage.md), [messaging](../capabilities/messaging.md), or [caching](../capabilities/caching.md). Persist audit/idempotency state when required; never store/log provider credentials or full sensitive payloads.

**Finish this step when:** Duplicate requests, cross-user access, and provider failure cannot produce an uncontrolled side effect or secret leak.

**Go next:** Step 6.

## Step 6 · Test and deliver

**What:** Prove the service without depending on the real provider.

**Where:** Create tests under `src/test/java/com/company/project/payment/`; edit `application.yml`, root CI/deployment files, and `<project-root>/README.md`; run commands at `<project-root>`.

**Do now:** Test success, malformed response, validation rejection, timeout, rate limit, authentication failure, retry exhaustion, duplicate request, and outage using the [testing guide](../docs/testing-guide.md).

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

**Finish this step when:** Tests run offline; failures cannot block forever; duplicate effects are controlled; a clean environment can deploy the adapter settings externally.

**Go next:** Release, or return to Step 1 for another provider operation.
