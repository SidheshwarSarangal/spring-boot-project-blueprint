# 00 · Learning roadmap

[← README](../README.md) · [Next: Mental model →](01-mental-model.md)

## The path from zero to a working project

```mermaid
flowchart TB
    A["Stage 1<br/>Java essentials"] --> B["Stage 2<br/>Spring mental model"]
    B --> C["Stage 3<br/>Create and run"]
    C --> D["Stage 4<br/>Build one feature"]
    D --> E["Stage 5<br/>Database + validation"]
    E --> F["Stage 6<br/>Tests"]
    F --> G["Stage 7<br/>Security + operations"]
    G --> H["Stage 8<br/>Real project"]
```

| Stage | Learn | Prove it |
|---|---|---|
| 1 | Classes, records, interfaces, collections, exceptions | Read the Task DTOs and service |
| 2 | Beans, injection, annotations, auto-configuration | Trace the startup diagram |
| 3 | Initializr, Maven, packages, application entry point | Run the app and health endpoint |
| 4 | Controller → service → repository | Create and fetch a task |
| 5 | Entity, JPA, validation, errors, transactions | Send valid and invalid requests |
| 6 | Unit, slice, and integration tests | Run `mvn test` |
| 7 | Profiles, secrets, security, Actuator | Explain what changes for production |
| 8 | Requirements, integrations, deployment | Plan your own project with the checklist |

## Minimum Java before Spring

```mermaid
mindmap
  root(("Java essentials"))
    Syntax
      Variables
      Methods
      Conditions
      Loops
    Types
      Class
      Record
      Enum
      Interface
    Collections
      List
      Set
      Map
    Behavior
      Exceptions
      Generics
      Lambdas
      Optional
    Tooling
      JDK
      Maven
      IDE debugger
```

You do **not** need advanced Java before starting. Learn each item when the reference app makes it concrete.

## Read code in this order

```mermaid
flowchart LR
    Main["TaskboardApplication"] --> DTO["Request / response DTOs"]
    DTO --> Controller["TaskController"]
    Controller --> Service["TaskService"]
    Service --> Repository["TaskRepository"]
    Repository --> Entity["Task"]
    Service --> Mapper["TaskMapper"]
    Controller --> Errors["ApiExceptionHandler"]
    Errors --> Tests["Tests"]
```

This order follows a request from the outside inward.

## Four-week practice route

```mermaid
timeline
    title Beginner practice route
    Week 1 : Java basics : Run Taskboard : Trace GET and POST
    Week 2 : Add priority field : Add validation : Add repository query
    Week 3 : Add project entity : Connect tasks to projects : Test relationships
    Week 4 : Add authentication : PostgreSQL + migration : Package and deploy
```

## Progress checklist

- [ ] I can explain controller, service, repository, entity, and DTO.
- [ ] I can trace a request to the database and back.
- [ ] I can add a field across request, entity, response, and tests.
- [ ] I can add a new endpoint without placing rules in the controller.
- [ ] I can explain why a transaction belongs around a use case.
- [ ] I can run unit, MVC, JPA, and integration tests.
- [ ] I know what belongs in configuration instead of source code.
- [ ] I can identify security and production work that a tutorial omits.

## When you get stuck

```mermaid
flowchart TD
    Error["An error appears"] --> First["Read the first meaningful cause"]
    First --> Layer{"Which layer?"}
    Layer -->|"Compile"| Types["Imports, types, Java version"]
    Layer -->|"Startup"| Beans["Bean graph, config, port, DB"]
    Layer -->|"HTTP"| Route["Method, path, JSON, status"]
    Layer -->|"Database"| SQL["Entity, migration, connection, transaction"]
    Types --> Small["Reproduce with smallest command/test"]
    Beans --> Small
    Route --> Small
    SQL --> Small
```

Use [Troubleshooting](11-troubleshooting.md) as a symptom map—not as a list to memorize.
