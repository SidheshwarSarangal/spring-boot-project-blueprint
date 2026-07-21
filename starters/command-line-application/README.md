# Command-line application starter

[← Command-line process](../../paths/command-line-application.md) · [Adapt this starter](../../docs/starter-adaptation-guide.md)

Run in this folder:

```bash
./mvnw clean verify
./mvnw spring-boot:run -Dspring-boot.run.arguments="--name=customer-a"
```

The command validates `--name`, delegates to `ImportService`, prints one result, and exits without starting a web server.
