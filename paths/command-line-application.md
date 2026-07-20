# Process: Build a command-line application

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this for terminal tools, administration, and small one-off automation.

## Step 1 · Define one command

> 📍 Create or edit `<project-root>/PROJECT.md`, section **5. Feature sheet**.

Record command syntax, required/optional values, stdin/file input, stdout result, stderr diagnostics, success/failure exit codes, confirmation, and partial-failure recovery.

```text
java -jar import-tool.jar --file=customers.csv --dry-run=true
Exit 0: completed
Exit 2: invalid arguments
Exit 3: input/processing failure
```

Before continuing, check: A shell script could call the command and decide success solely from documented output/exit code.

Continue to Step 2.

## Step 2 · Generate and package foundation

> 📍 Open [Spring Initializr](https://start.spring.io/) in the browser. After extracting the project, open a terminal in `<project-root>/`, the folder containing `pom.xml` and `mvnw`.

Generate minimal Spring Boot; do not add Spring Web unless the command also serves HTTP. Add only required data/provider dependencies.

```bash
./mvnw clean verify
```

Before continuing, check: JAR is created under `target/` without starting a web server.

Continue to Step 3.

## Step 3 · Create runner, options, and service

> 📍 Under `src/main/java/com/company/project/`, create the `command/` folder and these files. Replace `com/company/project` with the package selected in Initializr.

```text
src/main/java/com/company/project/command/
├── ImportCommand.java
├── ImportOptions.java
└── ImportService.java
```

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

Before continuing, check: Package and run valid/invalid arguments; valid reaches service, invalid fails before side effects.

Continue to Step 4.

## Step 4 · Make exit and rerun behavior reliable

> 📍 Create `src/main/java/com/company/project/command/CommandExitCodeConfiguration.java`. Edit `src/main/java/com/company/project/command/ImportCommand.java` and `ImportService.java`.

Map known failures to documented non-zero exit codes with `ExitCodeGenerator`/application exit handling; send diagnostics to stderr; add dry-run/confirmation for risky changes; make operations idempotent or checkpointed.

Before continuing, check: Shell observes exact exit codes; interruption/partial failure has documented safe rerun behavior.

Continue to Step 5.

## Step 5 · Attach required capabilities

> 📍 Create only the required linked package under `src/main/java/com/company/project/`: feature persistence files, `provider/`, or `file/`. Edit `src/main/java/com/company/project/command/ImportService.java` to call its interface.

Configure resources externally; validate availability before irreversible work; close/flush resources on exit. For large restartable record processing use the [batch process](batch-application.md).

Before continuing, check: Missing resource fails with documented exit code and no uncontrolled partial side effect.

Continue to Step 6.

## Step 6 · Test and deliver

> 📍 Create tests under `src/test/java/com/company/project/command/`; edit `<project-root>/README.md`, `src/main/resources/application.yml`, and root CI/release files; run commands at `<project-root>`.

Test argument parsing, stdout/stderr, exit codes, data/provider failure, partial work, interruption, and rerun using the [testing guide](../docs/testing-guide.md).

```bash
./mvnw clean verify
java -jar target/your-app.jar --file=sample.csv --dry-run=true
```

Follow relevant [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and [production](../docs/production-checklist.md) steps.

Before continuing, check: Clean build passes; documented command works in a clean environment; exit codes are stable.

Release, or return to Step 1 for another command.
