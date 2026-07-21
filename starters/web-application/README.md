# Web application starter

[← Web application process](../../paths/web-application.md) · [Adapt this starter](../../docs/starter-adaptation-guide.md)

Run in this folder:

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Open `http://localhost:8080/tasks/new`. Submit a blank title to see validation, then submit a title to reach the detail page.

Replace the in-memory `TaskService` with the [data-storage capability](../../capabilities/data-storage.md) when state must survive restart.
