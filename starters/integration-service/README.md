# Integration service starter

[← Integration process](../../paths/integration-service.md) · [Adapt this starter](../../docs/starter-adaptation-guide.md)

Run the safe local stub mode:

```bash
./mvnw clean verify
./mvnw spring-boot:run
curl -i -X POST http://localhost:8080/api/payments \
  -H 'Content-Type: application/json' \
  -d '{"reference":"order-1"}'
```

To call a real sandbox provider, implement its request/response contract in `HttpProviderAdapter`, then set `APP_PROVIDER_MODE=http` and `PROVIDER_BASE_URL` outside the repository.
