# Process: Build an external-system integration service

[← Choose another type](../README.md) · [External API](../capabilities/external-api.md) · [Testing](../docs/testing-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this when the main purpose is coordinating a payment, email, maps, AI, identity, or another provider.

## Step 1 · Define one integration action

**What:** Specify application behavior independently from the provider API.

**Where:** One feature sheet in `PROJECT.md`.

**Do:** Record caller/trigger, application input/output, provider operation, credential source, timeout, rate limit, retry safety, idempotency, and outage behavior.

**Verify:** The application result remains understandable without provider-specific field names.

**Next:** Step 2.

## Step 2 · Choose entry point and generate foundation

**What:** Select how work starts and produce a running project.

**Where:** Use [REST](rest-api.md) for an HTTP caller, [event](event-driven-service.md) for a broker trigger, or [background](background-worker.md) for timed synchronization. Then return here.

**Do:** Generate that path’s minimum dependencies plus Actuator. Do not add a second web/reactive stack without a requirement.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

**Verify:** Entry-point foundation starts before provider code exists.

**Next:** Step 3.

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

**Do:** Define an application-owned interface and adapter:

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

**Verify:** `./mvnw compile` passes and service depends on `PaymentProvider`, not the concrete adapter/provider DTO.

**Next:** Step 4.

## Step 4 · Handle provider outcomes safely

**What:** Bound calls and translate every expected provider outcome.

**Where:** HTTP client configuration, adapter, service, and application error handler.

**Do:** Configure connection/response timeouts; translate success/decline/rate-limit/auth/outage; retry only transient safe requests; use idempotency keys for retryable side effects; record safe correlation/provider IDs.

**Verify:** A stubbed timeout finishes within the configured bound and a decline becomes the intended application result—not a generic `500`.

**Next:** Step 5.

## Step 5 · Add required durability/security

**What:** Attach only state, security, asynchronous work, or caching required by the contract.

**Where:** Selected capability packages.

**Do:** Choose [security](../capabilities/security.md), [data storage](../capabilities/data-storage.md), [messaging](../capabilities/messaging.md), or [caching](../capabilities/caching.md). Persist audit/idempotency state when required; never store/log provider credentials or full sensitive payloads.

**Verify:** Duplicate requests, cross-user access, and provider failure cannot produce an uncontrolled side effect or secret leak.

**Next:** Step 6.

## Step 6 · Test and deliver

**What:** Prove the service without depending on the real provider.

**Where:** Stub-server integration tests, service tests, configuration, CI/deployment, README.

**Do:** Test success, malformed response, validation rejection, timeout, rate limit, authentication failure, retry exhaustion, duplicate request, and outage using the [testing guide](../docs/testing-guide.md).

```bash
./mvnw clean verify
```

Follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md).

**Verify:** Tests run offline; failures cannot block forever; duplicate effects are controlled; a clean environment can deploy the adapter settings externally.

**Next:** Release, or return to Step 1 for another provider operation.
