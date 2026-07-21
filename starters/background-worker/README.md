# Background worker starter

[← Background worker process](../../paths/background-worker.md) · [Adapt this starter](../../docs/starter-adaptation-guide.md)

Run in this folder:

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

The terminal logs one cleanup run every 10 seconds. Replace `CleanupService.expireOldItems()` with one bounded, repeatable use case and change `jobs.cleanup.cron` for the required schedule.
