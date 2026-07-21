# Batch application starter

[← Batch application process](../../paths/batch-application.md) · [Adapt this starter](../../docs/starter-adaptation-guide.md)

Run in this folder:

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

The `import-job` starts once, executes `import-step`, logs three imported sample items, and records execution metadata in an in-memory H2 database. Replace `ImportService.importSample()` with the required reader/processor/writer flow.
