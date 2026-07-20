# Beginner execution guide

[← Application selector](../README.md) · [Java syntax](java-syntax-primer.md) · [Troubleshooting](troubleshooting.md)

Use this page when a process says “create,” “edit,” “run,” or “verify” and you do not yet know the physical action. Examples use `com.company.project`; replace it with the base package generated for your application.

## Action A create the working repository and workbook

**Where:** Your normal projects folder in the file manager or terminal.

**Physically do:**

1. Create one folder named after the project, for example `orders-api`.
2. Open that folder in the editor/IDE.
3. Copy [`project-workbook.md`](project-workbook.md) into it and rename the copy `PROJECT.md`.
4. Open `PROJECT.md`, replace blank fields for the current feature, and save.
5. Keep only one feature marked `in progress`.

Terminal alternative:

```bash
mkdir orders-api
cd orders-api
```

Do not create Java packages before the generated Spring project is extracted into this folder.

**Verify:** The editor shows `<project-root>/PROJECT.md` and it contains one selected application type and one testable feature.

## Action B generate the Spring project in the browser

**Where:** Browser at [start.spring.io](https://start.spring.io/).

**Physically do:**

1. Choose **Maven**, **Java**, and **Jar**.
2. Choose the current stable Spring Boot release, not Snapshot/Milestone, unless the assignment requires one.
3. Choose a supported Java version.
4. Set **Group** to a reverse-domain name such as `com.company`.
5. Set **Artifact** to the project name such as `orders-api`.
6. Click **Add Dependencies** and select only those listed by the chosen application path/capabilities.
7. Click **Generate** and save the ZIP.
8. Extract the ZIP so its `pom.xml`, `mvnw`, and `src/` are directly under `<project-root>`, not under an accidental second nested folder.
9. Preserve `PROJECT.md` when extracting.

Correct:

```text
orders-api/
├── PROJECT.md
├── pom.xml
├── mvnw
└── src/
```

Incorrect:

```text
orders-api/orders-api/pom.xml
```

**Verify:** `<project-root>/pom.xml` and `<project-root>/src/main/java/...Application.java` exist.

## Action C open and import the generated Maven project

**Where:** IDE/editor.

**Physically do:**

1. Open the folder containing `pom.xml`—not only `src/`.
2. Trust/import it as a Maven project when prompted.
3. Select the same JDK major version chosen in Initializr.
4. Wait for dependency indexing/download to finish.
5. Open the integrated terminal and confirm its current directory contains `pom.xml`.

```bash
pwd
ls
java -version
./mvnw -version
```

On Windows PowerShell use:

```powershell
Get-Location
Get-ChildItem
java -version
.\mvnw.cmd -version
```

**Verify:** Maven reports the intended Java version and the IDE no longer shows unresolved Spring imports after indexing finishes.

## Action D run a command in the correct terminal

**Where:** Terminal whose working directory is `<project-root>` and contains `pom.xml`.

**Physically do:**

1. Save edited files.
2. Stop a currently running application with `Ctrl+C` when the command needs the same output/port.
3. Run exactly one command.
4. Read from the first `[ERROR]` or first exception—not only the last “BUILD FAILURE” line.

```bash
./mvnw compile
./mvnw test
./mvnw clean verify
./mvnw spring-boot:run
```

Use `compile` after creating a few main files, a focused test while fixing one behavior, and `clean verify` before commit/delivery.

**Verify:** Command exit is successful and the last Maven result is `BUILD SUCCESS`.

## Action E create a Java package and file

**Where:** `src/main/java` or matching `src/test/java` tree in the IDE.

**Physically do:**

1. Find the generated application class, for example:

   ```text
   src/main/java/com/company/project/ProjectApplication.java
   ```

2. Create feature packages below `com.company.project`, never beside/above it.
3. In the IDE, right-click the parent package → **New Package** → enter `task` or `task.dto`.
4. Right-click the package → **New Java Class/Record/Interface/Enum**.
5. Make the public type name exactly match the filename and capitalization.

```text
TaskService         → TaskService.java
CreateTaskRequest   → CreateTaskRequest.java
TaskRepository      → TaskRepository.java
```

6. Confirm the first line matches the folder:

```java
package com.company.project.task;
```

DTO example:

```java
package com.company.project.task.dto;
```

**Verify:** File is under the generated base package and `./mvnw compile` does not report package/type-name mismatch.

## Action F put a provided Java code block into a file

**Where:** The exact file named in the process step/action map.

**Physically do:**

1. Open or create the named file.
2. Keep/add the correct `package` line as the first non-comment line.
3. Paste only one public top-level type per file.
4. Replace the example domain consistently—`Task` → `Order` means filename, class, constructor, generics, method parameters, and imports all change.
5. Replace example fields/routes/properties with the values from `PROJECT.md`.
6. Let the IDE add imports. In IntelliJ place the cursor on the red type and press `Alt+Enter`; in VS Code use **Quick Fix / Organize Imports**.
7. Never import two different types with the same simple name. Check the package offered by the IDE.
8. Format the file using the IDE formatter.
9. Save and run `./mvnw compile` before creating many more files.

Expected file shape:

```java
package com.company.project.task;

import org.springframework.stereotype.Service;

@Service
public class TaskService {
    // fields
    // constructor
    // methods
}
```

Do not paste the Markdown fence lines that appear immediately above and below a displayed snippet into the Java file.

**Verify:** No red unresolved types remain and main-source compilation passes.

## Action G add or change a Maven dependency

**Where:** `<project-root>/pom.xml`, inside `<dependencies>...</dependencies>`.

**Physically do:**

1. Prefer selecting the dependency in Initializr during generation.
2. For an existing project, open `pom.xml` and add the dependency block inside the existing `<dependencies>` element—not inside `<build>` or after `</project>`.
3. Do not specify a version for dependencies managed by the Spring Boot parent unless official instructions require one.
4. Save, reload/reimport Maven, then run:

```bash
./mvnw dependency:tree
./mvnw compile
```

Structure:

```xml
<dependencies>
    <dependency>
        <groupId>...</groupId>
        <artifactId>...</artifactId>
    </dependency>
</dependencies>
```

**Verify:** Dependency appears in `dependency:tree`, imports resolve, and compilation passes.

## Action H edit YAML configuration

**Where:** `src/main/resources/application.yml` or the exact profile file named by the step.

**Physically do:**

1. Use spaces, never tabs.
2. Preserve indentation; child keys are normally two spaces deeper.
3. Do not create the same top-level key twice in one file; merge children under the existing key.
4. Quote values containing special characters when needed.
5. Reference secret/environment values rather than writing real values.

```yaml
spring:
  application:
    name: orders-api
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
```

Incorrect duplicate structure:

```yaml
spring:
  application:
    name: orders-api
spring:
  datasource:
    url: ...
```

Save and start the application. A YAML parse/bind error should be fixed before feature work continues.

**Verify:** Application starts and logs show the intended profile/configuration without printing secret values.

## Action I create a resource file

**Where:** Exact directory under `src/main/resources`.

**Physically do:**

- SQL migration: create `db/migration/V1__meaningful_name.sql`.
- GraphQL schema: create `graphql/schema.graphqls`.
- Thymeleaf page: create `templates/<feature>/<page>.html`.
- Static CSS/image: place under `static/`.
- Batch sample: place under a clearly named input/test resource folder.

Create missing folders in the IDE/file manager. Do not place Java files under `resources` or secrets inside packaged resources.

**Verify:** The resource appears at `target/classes/<same-relative-path>` after `./mvnw compile` when it should be packaged.

## Action J start the application and call it

**Where:** Terminal 1 at `<project-root>` for the application; Terminal 2 at any directory for `curl`/client calls.

**Physically do:**

Terminal 1:

```bash
./mvnw spring-boot:run
```

Wait for the “Started ...Application” log before calling it. Keep Terminal 1 visible for errors.

Terminal 2:

```bash
curl -i http://localhost:8080/actuator/health
```

For JSON:

```bash
curl -i -X POST http://localhost:8080/api/tasks \
  -H 'Content-Type: application/json' \
  -d '{"title":"Learn Spring"}'
```

Check status line, headers, body, Terminal 1 logs, and persisted/external result. Stop with `Ctrl+C`.

**Verify:** Actual status/body/state match the current feature contract—not merely “no exception.”

## Action K create and run a test

**Where:** Matching package under `src/test/java`.

**Physically do:**

1. Mirror the main package path.
2. Name the file `<ClassUnderTest>Test.java` or behavior/integration name.
3. Add one `@Test` for one observable case.
4. Arrange input/mocks, call one action, assert result.
5. Run focused test first:

```bash
./mvnw -Dtest=TaskServiceTest test
```

6. After it passes, run all tests and clean verification:

```bash
./mvnw test
./mvnw clean verify
```

Use the [testing guide](testing-guide.md) for scope-specific code.

**Verify:** The test fails when the protected behavior is intentionally broken and passes when correct.

## Action L fix the first compile or startup error

**Where:** Terminal output and first application-owned file named by the error.

**Physically do:**

1. Scroll to the first error/`Caused by`.
2. Copy only its exact class, file, line, and message into notes.
3. Open that file/line.
4. Check spelling/case, package, import, dependency, constructor arguments, braces, and config key.
5. Change one cause.
6. Rerun the smallest failing command.
7. Only after it passes, run `./mvnw clean verify`.

Do not randomly change versions or delete databases/build files before understanding the first cause. Use [Troubleshooting](troubleshooting.md).

**Verify:** Original smallest failure no longer reproduces and no new earlier failure replaces it.

## Action M save a clean checkpoint with Git

**Where:** Terminal at `<project-root>`.

**Physically do:**

```bash
./mvnw clean verify
git status --short
git diff --check
git diff
```

Review every intended file. Confirm secrets, `target/`, local DBs, logs, and IDE files are absent. Then stage/commit using the project’s approved workflow.

**Verify:** Clean build passes and Git shows only intended source/config/docs/test changes.
