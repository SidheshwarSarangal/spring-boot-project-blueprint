# Java survival sheet for the reference project

[Three-hour runbook](THREE-HOUR-ASSIGNMENT.md) · [Project workflow](docs/00-project-workflow.md) · [Working code](taskboard-api/src/main/java/com/example/taskboard)

This is not a Java course. Use it to read and adapt the patterns in this repository.

## Read a Java file

```java
package com.example.taskboard.task; // directory/namespace

import java.time.LocalDate;         // use a type from another package

public class TaskService {          // type declaration
    // fields, constructor, methods
}
```

One public top-level type normally lives in a file with the same name. `TaskService` belongs in `TaskService.java`.

## Types used here

| Syntax | Meaning | Typical use |
|---|---|---|
| `class` | Object with state and behavior | Entity, service, controller, mapper |
| `record` | Compact immutable data carrier | Request and response DTO |
| `interface` | Required operations without the implementation | Repository or provider boundary |
| `enum` | Fixed set of named values | Status such as `TODO`, `DONE` |

```java
public record CreateItemRequest(String name) {}

public enum ItemStatus {
    ACTIVE, ARCHIVED
}
```

> **Terms:** **Immutable** means values cannot be changed after creation. An **instance** is one object created from a class. A **method** is a named operation on a class or object.

## Fields, constructor, and methods

```java
public class ItemService {
    private final ItemRepository repository; // required field

    public ItemService(ItemRepository repository) { // constructor
        this.repository = repository;
    }

    public ItemResponse findById(Long id) { // method
        Item item = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
        return new ItemResponse(item.getId(), item.getName());
    }
}
```

- `private` means only this class can access it.
- `public` means other classes can access it.
- `final` means the field reference is assigned once.
- `this.repository` means the field on the current object.
- The type before a method name is its return type.
- `void` means the method returns no value.
- `new` creates an object.

## Common values and types

| Type | Use |
|---|---|
| `String` | Text |
| `Long` | Nullable whole number and common ID type |
| `long` | Non-null primitive whole number |
| `Integer` / `int` | Smaller whole number |
| `BigDecimal` | Money/precise decimal |
| `Boolean` / `boolean` | True or false |
| `LocalDate` | Date without time |
| `Instant` | UTC timestamp |
| `List<Item>` | Ordered collection of items |
| `Optional<Item>` | Item may be present or absent |
| `Page<Item>` | One page of a larger result |

`<Item>` is a **generic type argument**: it tells a collection/repository which type it contains.

## Conditions and exceptions

```java
if (name == null || name.isBlank()) {
    throw new IllegalArgumentException("Name is required");
}
```

```java
public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(Long id) {
        super("Item " + id + " was not found");
    }
}
```

- `== null` checks that no object exists.
- `||` means OR; `&&` means AND; `!` means NOT.
- `throw` stops the current path with an exception.
- `extends` inherits behavior from another class.
- A `RuntimeException` normally causes a Spring transaction to roll back.

## Lambdas and method references

```java
id -> repository.findById(id)
```

A **lambda** is a short function. The left side is input; the right side is the result/action.

```java
tasks.map(mapper::toResponse)
```

`mapper::toResponse` is a **method reference**. It means “call `mapper.toResponse` for each supplied value.”

Do not invent these expressions during a timed test; adapt the working pattern.

## Annotations used by the project

An **annotation** starts with `@` and adds framework metadata.

| Annotation | Put it on | Meaning |
|---|---|---|
| `@SpringBootApplication` | Main class | Start/configure the application |
| `@Entity` | Entity class | Map class to database persistence |
| `@Id` | Entity field | Primary key |
| `@RestController` | Controller class | Handle HTTP and return response bodies |
| `@RequestMapping` | Controller class | Base route |
| `@GetMapping`, `@PostMapping` | Controller method | HTTP method/route |
| `@RequestBody` | Method parameter | Read JSON body |
| `@PathVariable` | Method parameter | Read value from URL path |
| `@RequestParam` | Method parameter | Read query-string value |
| `@Valid` | Request parameter | Run validation constraints |
| `@Service` | Service class | Business-operation bean |
| `@Component` | General class | Spring-managed bean |
| `@Transactional` | Service method/class | Transaction boundary |
| `@RestControllerAdvice` | Error handler | Central controller exception mapping |

## Imports and packages

If the compiler says a type cannot be found:

1. Check spelling and capitalization.
2. Check the file’s `package` matches its directory.
3. Add the correct `import` using the IDE.
4. Confirm its dependency exists in `pom.xml`.
5. Re-run compilation and read the first error.

Java is case-sensitive: `TaskService`, `taskService`, and `taskservice` are different names.

## Error decoder

| Compiler message | Usually means |
|---|---|
| `cannot find symbol` | Misspelled name, missing import, or missing file |
| `package ... does not exist` | Wrong import/package or missing dependency |
| `incompatible types` | Assigned/returned the wrong type |
| `method ... cannot be applied` | Wrong argument count or types |
| `variable ... might not have been initialized` | Required value was never assigned |
| `reached end of file while parsing` | Missing `}` or other closing syntax |
| `class ... is public, should be declared in...` | Filename does not match public class |

Fix the first compiler error, then compile again. Later errors are often consequences of the first one.

## Safe adaptation rules

- Let the IDE rename a class when possible.
- Change package declaration before imports.
- Keep constructors when a class has required dependencies.
- Do not change annotation names from memory—copy a working example.
- Do not return an entity directly; use the response record.
- Compile frequently instead of editing every file before the first build.
- When unsure, prefer simple loops/methods over clever streams or abstractions.
