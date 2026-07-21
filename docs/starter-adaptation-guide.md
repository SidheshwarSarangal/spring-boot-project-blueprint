# Adapt a runnable starter safely

[← Application selector](../README.md) · [Runnable starters](../starters/README.md) · [Beginner execution guide](beginner-execution-guide.md) · [Troubleshooting](troubleshooting.md)

Use this guide when you want to learn from or copy one of the runnable examples. A starter demonstrates one complete technical flow; it is not a finished business application.

## 1. Choose only one starter

> 📍 Use the application table in [`starters/README.md`](../starters/README.md), then open the selected starter folder.

Choose by what starts the work and what result users receive—not by a business label such as shopping, hospital, or banking. For example, a shopping system may begin as a REST API and later add data storage, security, payments, and messaging.

Before continuing, check: You can explain why the selected application type matches the first feature in your own project.

## 2. Prove the unchanged starter works

> 📍 Open a terminal in the selected folder, where its `pom.xml` and `mvnw` are located.

Run the starter exactly as supplied before renaming or editing it:

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

Perform the observable check in that starter's README, then stop it with `Ctrl+C`. On Windows use `mvnw.cmd`.

If this clean checkpoint fails, use [troubleshooting](troubleshooting.md). Do not mix a setup problem with your own code changes.

Before continuing, check: The unchanged build ends with `BUILD SUCCESS`, the application starts, and its documented example produces the expected result.

## 3. Copy it into its own project folder

> 📍 Copy the selected starter directory—not the entire blueprint repository—to a new working directory.

The new project root must directly contain `pom.xml`, `mvnw`, and `src/`:

```text
my-project/
├── pom.xml
├── mvnw
├── mvnw.cmd
└── src/
```

Copy [`project-workbook.md`](project-workbook.md) into that root as `PROJECT.md`. Record the first feature's input, output, errors, stored data, access rules, and verification cases.

Before continuing, check: The copied project still passes `./mvnw clean verify` and `PROJECT.md` describes only one in-progress feature.

## 4. Rename project identity with refactoring tools

> 📍 Use the IDE's **Rename/Refactor** action for Java packages and types; edit Maven and configuration names in their own files.

Change one category at a time and compile after each category:

1. Rename the base package `com.example.starter` to your reverse-domain package, such as `com.acme.orders`.
2. Keep the `*Application` class at the root of that package so Spring scans its child packages.
3. Rename the application class and its same-named test.
4. In `pom.xml`, change `groupId`, `artifactId`, `name`, and `description`; do not change dependency versions casually.
5. Change `spring.application.name` in `application.properties` or `application.yml`.

Use the IDE refactor operation rather than search-and-replace for Java packages. It updates directories, package declarations, imports, and references together.

```bash
./mvnw compile
./mvnw test
```

Before continuing, check: No source file imports `com.example.starter`, the application class remains above all feature packages, and tests pass.

## 5. Replace the example with one vertical feature

> 📍 Work inside one feature package under `src/main/java/<base-package>/` and its matching test package.

Trace the supplied flow before changing it:

```text
entry point (HTTP, message, schedule, job, socket, or command)
→ application service
→ repository or external adapter when required
→ observable result
```

Rename the example feature with the IDE, then adapt its contract, types, service behavior, entry point, and tests. Keep the layers connected and make the smallest end-to-end behavior work before adding another feature.

Do not merely rename `Task` to `Order` while leaving task fields and validation behind. Replace filenames, type names, routes, configuration keys, request/response fields, test names, and assertions consistently.

Before continuing, check: One real input reaches the service and produces the result defined in `PROJECT.md`; invalid input has a deliberate outcome.

## 6. Add capabilities one at a time

> 📍 Open only the capability required by the current feature, complete it, then return here.

- [Data storage](../capabilities/data-storage.md)
- [Security](../capabilities/security.md)
- [External API](../capabilities/external-api.md)
- [Messaging](../capabilities/messaging.md)
- [File storage](../capabilities/file-storage.md)
- [Caching](../capabilities/caching.md)

Compile and test after each dependency or configuration change. Keep passwords, tokens, and real connection values outside Git.

Before continuing, check: The feature works with the capability, its expected failure is tested, and removing a secret from the environment fails safely.

## 7. Finish the project-specific documentation

> 📍 Update the new project's `README.md`, `PROJECT.md`, configuration examples, and automated tests.

Replace the starter README with instructions for your actual application: purpose, prerequisites, configuration variables, local dependencies, build/run/test commands, example input/output, health check, and known limitations.

Then follow the [configuration guide](configuration-guide.md), [testing guide](testing-guide.md), [delivery guide](delivery-guide.md), and [production checklist](production-checklist.md).

Before continuing, check: A new developer can clone the project, configure it without receiving secrets from Git, run it, observe the first feature, execute the tests, and understand what remains unfinished.
