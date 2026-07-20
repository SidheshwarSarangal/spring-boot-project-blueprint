# Path: Server-rendered web application

[← Choose another type](../README.md) · [Troubleshooting](../docs/troubleshooting.md)

Choose this when Spring returns HTML pages and processes browser forms, commonly with Thymeleaf.

## 1. Define the first page flow

```text
User opens: GET /tasks/new
User submits: POST /tasks
Valid result: redirect to /tasks/{id}
Invalid result: show the form with field errors
Access: state who may view and submit
```

Record it in the [project workbook](../docs/project-workbook.md).

## 2. Generate the project

Select:

- Spring Web;
- Thymeleaf;
- Validation;
- Actuator.

Add [data storage](../capabilities/data-storage.md) and [security](../capabilities/security.md) when required.

```bash
./mvnw clean verify
./mvnw spring-boot:run
```

## 3. Build one page flow

```text
form object → @Controller → service → repository/adapter
→ model → Thymeleaf template → browser
```

1. Create a form object with validation.
2. Create a service method containing the business action.
3. Add a `GET` controller method to render the form.
4. Add a `POST` method to validate and submit it.
5. On validation failure, return the same template with errors.
6. On success, use Post/Redirect/Get.
7. Keep business rules out of controllers and templates.

Place templates in `src/main/resources/templates/` and static CSS/images in `src/main/resources/static/`.

## 4. Attach required capabilities

- [Data storage](../capabilities/data-storage.md)
- [Security](../capabilities/security.md)—normally required for private forms/pages; retain CSRF protection
- [External API](../capabilities/external-api.md)
- [File storage](../capabilities/file-storage.md)
- [Caching](../capabilities/caching.md)

## 5. Verify

Test page rendering, form binding, validation messages, successful redirects, permissions, and escaped user content. Manually complete the browser flow.

```bash
./mvnw clean verify
```

## 6. Finish

Repeat for the next page flow, then complete the [production checklist](../docs/production-checklist.md).

Done means every page has defined access, forms behave safely on success/failure, templates contain presentation only, and the application starts from documented commands.
