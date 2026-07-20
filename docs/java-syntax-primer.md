# Java syntax primer for adapting handbook code

[← Beginner actions](beginner-execution-guide.md) · [Spring foundation](java-spring-foundation.md) · [Working Java](../taskboard-api/src/main/java/com/example/taskboard)

Use this page when code syntax is unfamiliar. It is intentionally limited to constructs used by this handbook.

## 1. Package, import, and public type

```java
package com.company.project.task;

import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
}
```

- `package` matches the directory after `src/main/java`.
- `import` makes a type from another package available.
- `public class TaskService` declares a type named exactly like `TaskService.java`.
- Java is case-sensitive.
- Statements usually end with `;`; class/method bodies use `{ ... }`.

## 2. Class, record, interface, and enum

```java
public class Task { /* mutable entity/state */ }

public record TaskResponse(Long id, String title) {}

public interface PaymentProvider {
    PaymentResult charge(PaymentCommand command);
}

public enum TaskStatus {
    TODO, DONE
}
```

- Class: state and behavior.
- Record: compact immutable data carrier; access value with `response.id()`.
- Interface: operations without provider-specific implementation.
- Enum: fixed named values; compare with `status == TaskStatus.DONE`.

## 3. Field and constructor injection

```java
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }
}
```

- Field type is `TaskRepository`; field name is `repository`.
- `final` means assign once.
- Constructor has the same name as the class and no return type.
- `this.repository` is the field; `repository` is the constructor parameter.

## 4. Method declaration and call

```java
public TaskResponse findById(Long id) {
    Task task = repository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException(id));
    return mapper.toResponse(task);
}
```

Read it as:

```text
visibility public
return type TaskResponse
method name findById
parameter Long id
local variable Task task
return one TaskResponse
```

Call it with `service.findById(7L)`. `7L` is a `long` literal suitable for `Long` conversion.

## 5. Generics and collections

```java
List<Task> tasks;
Optional<Task> maybeTask;
Page<TaskResponse> page;
Map<String, String> values;
```

`<Task>` states the contained type. Common operations:

```java
tasks.add(task);
tasks.get(0);
tasks.isEmpty();
maybeTask.orElseThrow(...);
page.getContent();
values.get("key");
```

Do not return an unbounded `List` of every database row; use pagination.

## 6. Conditions, boolean operators, and null

```java
if (title == null || title.isBlank()) {
    throw new IllegalArgumentException("title is required");
}

if (task.isOwnedBy(user.id()) && !user.isAdmin()) {
    // both ownership and not-admin condition are true
}
```

- `== null`: no object.
- `||`: OR.
- `&&`: AND.
- `!`: NOT.
- Use `.equals(...)` for object value equality when null handling is known; do not use `==` for ordinary String equality.

## 7. Exceptions

```java
public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super("Task " + id + " was not found");
    }
}
```

`throw` creates a failure path. `extends RuntimeException` makes it unchecked; Spring transactions normally roll back for unchecked exceptions. Catch an exception only when this layer can translate, recover, or add useful context.

## 8. Lambda and method reference

```java
id -> repository.findById(id)
tasks.forEach(task -> task.complete());
page.map(mapper::toResponse);
```

- Lambda: input on the left of `->`, action/result on the right.
- `mapper::toResponse` means call that method for each supplied value.
- Prefer an ordinary named method if the lambda becomes hard to read.

## 9. Annotation syntax

```java
@PostMapping("/{id}")
TaskResponse find(@PathVariable Long id) { ... }
```

Annotations add framework metadata. Their position matters: class annotation configures the type; method annotation configures a method; parameter annotation configures binding/validation for that parameter.

Common annotations:

| Annotation | Put on | Meaning |
|---|---|---|
| `@SpringBootApplication` | main class | start/configure/scan application |
| `@RestController` | class | HTTP + response body |
| `@Controller` | class | HTTP + views/templates |
| `@Service` | class | business-use-case bean |
| `@Component` | class | general bean |
| `@Configuration` | class | bean/config declarations |
| `@Bean` | method | method result becomes a bean |
| `@Entity` | class | relational persistent type |
| `@Transactional` | service method/class | transaction boundary |
| `@Valid` | controller/GraphQL input | run Jakarta validation |
| `@Test` | test method | executable test |

## 10. Record validation

```java
public record CreateTaskRequest(
    @NotBlank @Size(max = 120) String title,
    @FutureOrPresent LocalDate dueDate
) {}
```

Validation annotations constrain boundary input. They require the Validation dependency and `@Valid` at the binding point.

## 11. Entity basics

```java
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    protected Task() {}
}
```

JPA requires an accessible no-argument constructor (commonly protected). Keep entity fields private and expose intentional getters/domain methods. `@Column` does not replace request validation or database migrations.

## 12. String, date, money, and time

```java
String name = "Alice";
LocalDate dueDate = LocalDate.parse("2030-01-01");
Instant now = Instant.now();
BigDecimal amount = new BigDecimal("19.99");
```

Use `BigDecimal`, not `double`, for money. Inject a `Clock` when time-dependent business rules need deterministic tests.

## 13. Access modifiers

| Modifier | Meaning |
|---|---|
| `public` | accessible from any package |
| `protected` | class/package/subclasses |
| no modifier | package-private |
| `private` | only inside the declaring type |

Use the narrowest access that supports required collaboration.

## 14. Safe rename/adaptation checklist

When adapting `Task` to `Order`:

1. Rename file/type with IDE refactor.
2. Change package only if folder changes.
3. Rename constructor exactly with class.
4. Update generic types such as `JpaRepository<Order, Long>`.
5. Replace fields and validation from `PROJECT.md`.
6. Update mapper, service parameters/return values, controller route, and tests.
7. Organize imports.
8. Compile after every two or three related files.

## 15. First compiler-error decoder

| Error | Usually do |
|---|---|
| `cannot find symbol` | check spelling/case/import/file/dependency |
| `package ... does not exist` | fix import/package or add dependency |
| `incompatible types` | compare declared/returned/assigned types |
| `method ... cannot be applied` | compare argument count/types |
| `variable ... might not have been initialized` | assign required constructor/local value |
| `reached end of file while parsing` | add missing `}`/closing syntax |
| `class ... is public, should be declared in...` | rename file to public type |

Fix the first error, save, and compile again.
