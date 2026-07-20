# API gateway starter

Run in this folder:

```bash
./mvnw clean verify
./mvnw spring-boot:run
curl -i http://localhost:8080/actuator/health
```

Requests under `/service/**` are routed to `DOWNSTREAM_URL`, defaulting to `http://localhost:9000`, after removing `/service`. The filter preserves or creates `X-Correlation-Id`.
