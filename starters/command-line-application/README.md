# Command-line application starter

Run in this folder:

```bash
./mvnw clean verify
./mvnw spring-boot:run -Dspring-boot.run.arguments="--name=customer-a"
```

The command validates `--name`, delegates to `ImportService`, prints one result, and exits without starting a web server.
