# 01 · Node-to-Spring structure lookup

[← Project workflow](00-project-workflow.md) · [Repository home](../README.md) · [Working example](../taskboard-api/README.md)

## Node/Express → Spring Boot

| Node/Express idea | Spring Boot equivalent | Important difference |
|---|---|---|
| `package.json` | `pom.xml` | Maven also defines compilation and packaging |
| `npm install` | `mvn dependency:resolve` / build | Maven downloads declared artifacts |
| `node server.js` | `mvn spring-boot:run` | Boot creates the application context and embedded server |
| `app.get(...)` | `@GetMapping` in `@RestController` | Mapping is declared on classes/methods |
| Middleware | Servlet filters / interceptors / advice | Each runs at a different boundary |
| Controller function | Controller method | Spring binds request data to typed parameters |
| Service module | `@Service` class | Usually injected through the constructor |
| ORM model | JPA `@Entity` | Entity maps Java state to a relational table |
| Data access module | Spring Data repository | Spring can implement the interface at runtime |
| Request schema | DTO + Jakarta Validation | Java types and constraints define the boundary |
| Error middleware | `@RestControllerAdvice` | Central exception-to-response mapping |
| `.env` / config | Properties, YAML, env vars, profiles | Boot has ordered external configuration sources |
| Jest/Supertest | JUnit, Mockito, MockMvc, test slices | Boot can load only the layer under test |

## What happens at startup

```mermaid
sequenceDiagram
    participant JVM
    participant Main as main()
    participant Boot as SpringApplication
    participant Context as ApplicationContext
    participant Auto as Auto-configuration
    participant Server as Embedded Tomcat

    JVM->>Main: Start application
    Main->>Boot: run(TaskboardApplication.class)
    Boot->>Context: Create application context
    Context->>Context: Scan components
    Auto->>Context: Add beans based on classpath + properties
    Context->>Context: Resolve constructor dependencies
    Context->>Server: Start web server
    Server-->>JVM: Listening on port 8080
```

## Dependency injection

Without injection:

```java
TaskRepository repository = new TaskRepositorySomehow();
TaskService service = new TaskService(repository, new TaskMapper());
TaskController controller = new TaskController(service);
```

With Spring:

```java
@RestController
class TaskController {
    private final TaskService service;

    TaskController(TaskService service) {
        this.service = service;
    }
}
```

```mermaid
flowchart LR
    C["TaskController constructor"] -->|"needs"| S["TaskService bean"]
    S -->|"needs"| R["TaskRepository bean"]
    S -->|"needs"| M["TaskMapper bean"]
    Spring["ApplicationContext"] --> C
    Spring --> S
    Spring --> R
    Spring --> M
```

Constructor injection makes required collaborators visible and makes classes easy to unit-test.

## Annotation map

```mermaid
flowchart TB
    A["Annotations"]
    A --> Boot["@SpringBootApplication<br/>start + configure + scan"]
    A --> Web["@RestController<br/>HTTP boundary"]
    A --> Service["@Service<br/>business use cases"]
    A --> Component["@Component<br/>general managed object"]
    A --> Entity["@Entity<br/>persistent object"]
    A --> Tx["@Transactional<br/>transaction boundary"]
    A --> Valid["@Valid<br/>validate nested input"]
    A --> Advice["@RestControllerAdvice<br/>central errors"]
```

Annotations describe roles or behavior. They do not replace design decisions.

## Automatic does not mean magic

```mermaid
flowchart LR
    Classpath["Dependencies in pom.xml"] --> Conditions["Auto-config conditions"]
    Properties["application.yml + env"] --> Conditions
    YourBeans["Beans you define"] --> Conditions
    Conditions --> Result["Beans Boot creates"]
```

Example: Web MVC on the classpath triggers MVC configuration; JPA plus a database driver triggers a `DataSource`, entity manager, and repository support.

## Request binding

```mermaid
flowchart LR
    HTTP["HTTP request"] --> Path["@PathVariable"]
    HTTP --> Query["@RequestParam"]
    HTTP --> Body["@RequestBody"]
    Body --> Jackson["Jackson JSON → Java"]
    Jackson --> Valid["@Valid constraints"]
    Valid --> Method["Controller method"]
```

## The three shapes rule

```mermaid
flowchart LR
    Request["Request DTO<br/>what client may send"] --> Entity["Entity<br/>what database stores"]
    Entity --> Response["Response DTO<br/>what client may see"]
```

These shapes can look similar and still serve different purposes. Keeping them separate prevents database changes from silently changing the public API.
