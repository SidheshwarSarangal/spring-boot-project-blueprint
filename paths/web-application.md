# Process: Build a server-rendered web application

[← Choose another type](../README.md) · [Testing](../docs/testing-guide.md) · [Configuration](../docs/configuration-guide.md) · [Troubleshooting](../docs/troubleshooting.md)

> New to Java or Spring Boot? Complete the [foundation](../docs/java-spring-foundation.md) once.

Use this process when Spring returns HTML pages and processes browser forms, commonly with Thymeleaf.

## Step 1 · Define one page flow

**What:** Specify one browser journey with success and validation behavior.

**Where:** Create `<project-root>/PROJECT.md`. Copy one feature sheet from the [workbook](../docs/project-workbook.md) into it.

**Do now:** Record:

```text
GET  /tasks/new     → render empty form
POST /tasks         → validate and create
Success             → redirect to /tasks/{id}
Invalid             → redisplay form and field errors
Access              → authenticated member
```

**Finish this step when:** Expected page, form fields, redirect, errors, and access are unambiguous.

**Go next:** Step 2.

## Step 2 · Generate and run the foundation

**What:** Create a working web foundation.

**Where:** Use [Spring Initializr](https://start.spring.io/) in the browser. Run every command below in `<project-root>/`, the directory containing `pom.xml` and `mvnw`.

**Do now:** Select Spring Web, Thymeleaf, Validation, and Actuator; add only required capabilities.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

**Finish this step when:** Application starts and `http://localhost:8080/actuator/health` returns `UP`.

**Go next:** Step 3.

## Step 3 · Create the page-flow files

**What:** Establish form → controller → service → template.

**Where:** Create these paths; replace `com/company/project` with the package selected in Initializr.

```text
src/main/java/com/company/project/task/
├── TaskForm.java
├── TaskController.java
└── TaskService.java
src/main/resources/templates/tasks/
├── form.html
└── detail.html
src/main/resources/static/css/app.css
```

**Do now:** Create a validated form object:

```java
public record TaskForm(
    @NotBlank @Size(max = 120) String title,
    @FutureOrPresent LocalDate dueDate
) {}
```

**Finish this step when:** Files are under the application root package/resources and `./mvnw compile` passes.

**Go next:** Step 4.

## Step 4 · Implement GET, POST, and template

**What:** Render, validate, submit, and redirect one form.

**Where:** Edit `src/main/java/com/company/project/task/TaskController.java`, `TaskService.java`, and `src/main/resources/templates/tasks/form.html`.

**Do now:**

```java
@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping("/new")
    String form(Model model) {
        model.addAttribute("taskForm", new TaskForm("", null));
        return "tasks/form";
    }

    @PostMapping
    String create(@Valid @ModelAttribute TaskForm taskForm,
                  BindingResult errors) {
        if (errors.hasErrors()) return "tasks/form";
        Long id = service.create(taskForm);
        return "redirect:/tasks/" + id;
    }
}
```

```html
<form th:action="@{/tasks}" th:object="${taskForm}" method="post">
  <input th:field="*{title}" />
  <p th:if="${#fields.hasErrors('title')}" th:errors="*{title}"></p>
  <input type="date" th:field="*{dueDate}" />
  <button type="submit">Create</button>
</form>
```

Keep business rules in the service. Use Post/Redirect/Get after success.

**Finish this step when:** Open `/tasks/new`; invalid form stays with errors; valid form redirects and produces the intended result.

**Go next:** Step 5.

## Step 5 · Add required capabilities and secure rendering

**What:** Attach only necessary persistence/integrations and protect pages/forms.

**Where:** Add capability files only in the paths named by the selected capability guide. Apply access rules in `src/main/java/com/company/project/security/SecurityConfiguration.java`; keep feature logic in `task/` and HTML in `src/main/resources/templates/tasks/`.

**Do now:** Choose only required modules: [data storage](../capabilities/data-storage.md), [security](../capabilities/security.md), [external API](../capabilities/external-api.md), [file storage](../capabilities/file-storage.md), or [caching](../capabilities/caching.md). Retain CSRF protection for authenticated browser forms and let Thymeleaf escape user content.

**Finish this step when:** Public/protected pages match the access rules; CSRF, cross-user access, and user-supplied text behave safely.

**Go next:** Step 6.

## Step 6 · Test and deliver

**What:** Prove page behavior and create a deployable artifact.

**Where:** Create tests in `src/test/java/com/company/project/task/`. Edit `src/main/resources/application.yml`, `<project-root>/README.md`, and the CI/deployment file required by the chosen platform. Run commands in `<project-root>/`.

**Do now:** Test view name/model, form binding, validation, redirect, CSRF, allowed/forbidden access, and service rules using the [testing guide](../docs/testing-guide.md).

```bash
./mvnw clean verify
```

Then follow [configuration](../docs/configuration-guide.md), [delivery](../docs/delivery-guide.md), and the [production checklist](../docs/production-checklist.md).

**Finish this step when:** Clean build passes and a clean environment can complete the browser flow with correct security and health.

**Go next:** Release and monitor, or return to Step 1 for the next page flow.
