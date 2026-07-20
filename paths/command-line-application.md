# Process: Build a command-line application

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this for terminal tools, administration, and small one-off automation.

## Repository action map

| Step | Exact location | Add or run there |
|---|---|---|
| 1 | Create `<project-root>/PROJECT.md` | Command/options/output/exit contract |
| 2 | Browser: Initializr; Terminal: generated `<project-root>` | Generate/build JAR |
| 3 | Create `src/main/java/com/company/project/command/`; Terminal: project root | Runner/options/service code and command run |
| 4 | Create/edit exception/exit-code config and service transaction | Exit/rerun behavior |
| 5 | Create/edit selected data/provider/file package | Required resources |
| 6 | Create matching `src/test/java/.../command/`; edit README/config/CI | CLI tests and delivery |

**Beginner actions by step:** 1 → [A workbook](../docs/beginner-execution-guide.md#action-a-create-the-working-repository-and-workbook); 2 → [B generate](../docs/beginner-execution-guide.md#action-b-generate-the-spring-project-in-the-browser), [D terminal](../docs/beginner-execution-guide.md#action-d-run-a-command-in-the-correct-terminal); 3–5 → [E create files](../docs/beginner-execution-guide.md#action-e-create-a-java-package-and-file), [F add code](../docs/beginner-execution-guide.md#action-f-put-a-provided-java-code-block-into-a-file), [D run JAR](../docs/beginner-execution-guide.md#action-d-run-a-command-in-the-correct-terminal); 6 → [K tests](../docs/beginner-execution-guide.md#action-k-create-and-run-a-test), [M checkpoint](../docs/beginner-execution-guide.md#action-m-save-a-clean-checkpoint-with-git).

## Step 1 · Define one command

**What:** Specify arguments, output, exit codes, side effects, and rerun behavior.

**Where:** One feature sheet in `PROJECT.md`.

**Do:** Record command syntax, required/optional values, stdin/file input, stdout result, stderr diagnostics, success/failure exit codes, confirmation, and partial-failure recovery.

```text
java -jar import-tool.jar --file=customers.csv --dry-run=true
Exit 0: completed
Exit 2: invalid arguments
Exit 3: input/processing failure
```

**Verify:** A shell script could call the command and decide success solely from documented output/exit code.

**Next:** Step 2.

## Step 2 · Generate and package foundation

**What:** Build a minimal non-web executable JAR.

**Where:** Spring Initializr and project root.

**Do:** Generate minimal Spring Boot; do not add Spring Web unless the command also serves HTTP. Add only required data/provider dependencies.

```bash
./mvnw clean verify
```

**Verify:** JAR is created under `target/` without starting a web server.

**Next:** Step 3.

## Step 3 · Create runner, options, and service

**What:** Parse input then delegate one action to a service.

**Where:**

```text
src/main/java/com/company/project/command/
├── ImportCommand.java
├── ImportOptions.java
└── ImportService.java
```

**Do:**

```java
@Component
class ImportCommand implements ApplicationRunner {
    private final ImportService service;

    ImportCommand(ImportService service) {
        this.service = service;
    }

    public void run(ApplicationArguments args) {
        if (!args.containsOption("file")) {
            throw new InvalidCommandException("--file is required");
        }
        Path file = Path.of(args.getOptionValues("file").get(0));
        ImportResult result = service.importFile(file);
        System.out.println("Imported " + result.count() + " records");
    }
}
```

Keep terminal parsing/printing here; business/file/provider rules belong in the service/adapters.

**Verify:** Package and run valid/invalid arguments; valid reaches service, invalid fails before side effects.

**Next:** Step 4.

## Step 4 · Make exit and rerun behavior reliable

**What:** Return stable exit codes and control partial side effects.

**Where:** Exception/exit-code configuration, service transaction, dry-run/idempotency logic.

**Do:** Map known failures to documented non-zero exit codes with `ExitCodeGenerator`/application exit handling; send diagnostics to stderr; add dry-run/confirmation for risky changes; make operations idempotent or checkpointed.

**Verify:** Shell observes exact exit codes; interruption/partial failure has documented safe rerun behavior.

**Next:** Step 5.

## Step 5 · Attach required capabilities

**What:** Add only external resources used by the command.

**Where:** Selected [data](../capabilities/data-storage.md), [external API](../capabilities/external-api.md), or [file](../capabilities/file-storage.md) package.

**Do:** Configure resources externally; validate availability before irreversible work; close/flush resources on exit. For large restartable record processing use the [batch process](batch-application.md).

**Verify:** Missing resource fails with documented exit code and no uncontrolled partial side effect.

**Next:** Step 6.

## Step 6 · Test and deliver

**What:** Make the command scriptable and distributable.

**Where:** Runner/service tests, README command examples, configuration, CI/release artifact.

**Do:** Test argument parsing, stdout/stderr, exit codes, data/provider failure, partial work, interruption, and rerun using the [testing guide](../docs/testing-guide.md).

```bash
./mvnw clean verify
java -jar target/your-app.jar --file=sample.csv --dry-run=true
```

Follow relevant [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md) steps.

**Verify:** Clean build passes; documented command works in a clean environment; exit codes are stable.

**Next:** Release, or return to Step 1 for another command.
