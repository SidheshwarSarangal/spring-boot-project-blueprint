# Path: Command-line application

[← Choose another type](../README.md) · [Troubleshooting](../docs/troubleshooting.md)

Choose this for terminal tools, administrative commands, and small one-off automations that benefit from Spring configuration and dependency injection.

## 1. Define the command

```text
Command and arguments/options:
Input source:
Output and exit code:
Side effects:
Invalid-input behavior:
Partial-failure/re-run behavior:
```

Record it in the [project workbook](../docs/project-workbook.md).

## 2. Generate the project

Generate a minimal Spring Boot project. Do not add Spring Web unless the command also runs an HTTP server. Add only required database/provider dependencies.

```bash
./mvnw clean verify
```

Continue only after the untouched project builds successfully.

## 3. Build one command

```text
arguments/options → command runner → service → repository/adapter
→ terminal output + exit code
```

1. Parse and validate arguments before side effects.
2. Keep terminal formatting separate from the service.
3. Return useful output to stdout and diagnostics to stderr.
4. Use non-zero exit codes for failure.
5. Make repeated execution safe or clearly require confirmation.
6. Avoid starting an embedded web server when it is not required.

## 4. Attach required capabilities

- [Data storage](../capabilities/data-storage.md)
- [External API](../capabilities/external-api.md)
- [File storage](../capabilities/file-storage.md)

For large restartable record processing, use the [batch path](batch-application.md) instead.

## 5. Verify

Test valid arguments, missing/invalid options, successful output/exit code, external/data failure, partial work, interruption, and safe rerun.

```bash
./mvnw clean verify
java -jar target/your-app.jar --required-option=value
```

## 6. Finish

Document install/run examples and exit codes, then apply the relevant parts of the [production checklist](../docs/production-checklist.md).

Done means the command is scriptable, produces reliable exit codes, validates before side effects, and is safe to retry or clearly documents why it is not.
