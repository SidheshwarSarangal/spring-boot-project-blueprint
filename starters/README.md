# Runnable starters

Choose the starter that matches the application path. Each folder is an independent Maven project generated from Spring Initializr and contains one complete, minimal flow.

New to adapting example code? Follow [Adapt a runnable starter safely](../docs/starter-adaptation-guide.md). Build the unchanged starter first, copy only one starter into a separate project folder, then rename and modify it in small verified steps.

| Application | Starter | First observable result |
|---|---|---|
| REST API | [Taskboard API](../taskboard-api/README.md) | JSON CRUD API backed by H2 |
| GraphQL API | [graphql-api](graphql-api/README.md) | GraphQL mutation and query |
| Web application | [web-application](web-application/README.md) | Validated Thymeleaf form |
| Background worker | [background-worker](background-worker/README.md) | Scheduled cleanup log |
| Event-driven service | [event-driven-service](event-driven-service/README.md) | Kafka publish/consume with duplicate guard |
| Integration service | [integration-service](integration-service/README.md) | HTTP endpoint using a replaceable provider adapter |
| API gateway | [api-gateway](api-gateway/README.md) | Route plus correlation-ID filter |
| Batch application | [batch-application](batch-application/README.md) | Spring Batch job and step |
| Real-time application | [realtime-application](realtime-application/README.md) | Browser WebSocket exchange |
| Command-line application | [command-line-application](command-line-application/README.md) | Parsed option and terminal result |

In a selected folder run:

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Do not combine all starters into one application. The adaptation guide covers safe package refactoring, project identity, feature replacement, capability selection, and the final documentation gate.
