# Troubleshooting

[← Start page](../README.md) · [Core guide](core-guide.md) · [Production checklist](production-checklist.md)

## Use this loop

1. Reproduce the smallest failure.
2. Read the first meaningful exception and its deepest `Caused by` message.
3. Identify the phase: compile, context startup, HTTP, database, or test.
4. Change one cause.
5. Rerun the smallest relevant command, then `mvn clean verify`.

## Fast checks

```bash
java -version
mvn -version
mvn clean verify
mvn spring-boot:run
curl -i http://localhost:8080/actuator/health
```

| Symptom | Check first |
|---|---|
| `cannot find symbol` | spelling, import, package, dependency, first compiler error |
| `package ... does not exist` | package path and dependency in `pom.xml` |
| Application context fails | first bean named in the dependency chain |
| No bean found | annotation, package below application class, required dependency |
| Port already in use | stop the old process or configure another port |
| `404` | HTTP method, full controller path, application port |
| `400` | request JSON, content type, DTO types, validation response |
| `415` | send the supported `Content-Type`, normally `application/json` |
| `500` | server stack trace and first application-owned frame |
| Table/column missing | datasource URL, active migration, schema history |
| Lazy-loading error | map needed data inside the service transaction; query it deliberately |
| Too many SQL queries | inspect access pattern; use a fetch join, entity graph, or projection |
| Test passes in IDE only | run Maven from project root and check JDK/test configuration |

## Bean startup failures

Trace the chain from the top-level bean to the deepest constructor/configuration failure. Common causes are a missing component annotation, a class outside the scanned package, a missing property, or more than one bean matching the same interface.

## HTTP failures

Confirm the application started, then compare the request with the controller: method, class-level path, method-level path, headers, JSON field names, and Java field types. MVC tests are the quickest way to lock a correction.

## Database failures

Verify which datasource is active and inspect the first SQL exception. Do not delete a shared database to fix a migration. Correct the migration sequence or add a new forward migration.

## Ask for help with evidence

Provide the command, first meaningful error, relevant class/configuration, expected behavior, actual behavior, Java/Spring versions, and the smallest steps to reproduce. Remove credentials and tokens first.
