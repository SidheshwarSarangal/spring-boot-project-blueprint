# Real-time application starter

Run in this folder:

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Open `http://localhost:8080`, wait for `connected`, and send a message. The raw WebSocket endpoint at `/ws` returns `server: <message>`.
