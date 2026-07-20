# Troubleshooting

[← Start page](../README.md) · [Core guide](core-guide.md) · [Production checklist](production-checklist.md)

For the physical error-reading and rerun procedure, use [Action L](beginner-execution-guide.md#action-l-fix-the-first-compile-or-startup-error).

## Use this loop

| Step | What | Where | Do | Verify | Next |
|---|---|---|---|---|---|
| 1 | Small reproducible failure | Terminal/test/request | Remove unrelated actions | Failure repeats reliably | 2 |
| 2 | First real cause | First exception + deepest `Caused by` | Ignore consequence noise | Application-owned failing point is known | 3 |
| 3 | Failure phase | Compile/startup/HTTP/data/test | Select matching section below | Scope is narrowed | 4 |
| 4 | One correction | Relevant file/config | Change one suspected cause | Small reproduction passes | 5 |
| 5 | Regression proof | Project root | Run smallest test then clean verify | Clean build passes | Return to path |

If Step 5 fails, return to Step 1 with the new smallest failure.

## Fast checks

```bash
java -version
mvn -version
./mvnw clean verify
./mvnw spring-boot:run
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

On Windows use `mvnw.cmd`. If the project has no Maven wrapper, install Maven and replace `./mvnw` with `mvn`.

## Bean startup failures

Trace the chain from the top-level bean to the deepest constructor/configuration failure. Common causes are a missing component annotation, a class outside the scanned package, a missing property, or more than one bean matching the same interface.

## HTTP failures

Confirm the application started, then compare the request with the controller: method, class-level path, method-level path, headers, JSON field names, and Java field types. MVC tests are the quickest way to lock a correction.

## Database failures

Verify which datasource is active and inspect the first SQL exception. Do not delete a shared database to fix a migration. Correct the migration sequence or add a new forward migration.

## Ask for help with evidence

Provide the command, first meaningful error, relevant class/configuration, expected behavior, actual behavior, Java/Spring versions, and the smallest steps to reproduce. Remove credentials and tokens first.
