# Real-time application starter

[← Real-time process](../../paths/realtime-application.md) · [Adapt this starter](../../docs/starter-adaptation-guide.md)

Run in this folder:

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Open `http://localhost:8080`, wait for `connected`, and send a message. The raw WebSocket endpoint at `/ws` returns `server: <message>`.
