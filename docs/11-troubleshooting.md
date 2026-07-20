# 11 · Troubleshooting

[← Toolbox](10-real-project-toolbox.md) · [README](../README.md) · [Official references →](official-references.md)

## Diagnose by phase

```mermaid
flowchart TD
    Problem["Problem"] --> Phase{"When?"}
    Phase -->|"Compile"| Compile["Java, imports, types, dependencies"]
    Phase -->|"Startup"| Startup["Beans, properties, port, database"]
    Phase -->|"Request"| HTTP["Route, JSON, validation, security"]
    Phase -->|"Database"| DB["Mapping, query, transaction, migration"]
    Phase -->|"Production only"| Env["Profiles, secrets, network, resources"]
```

## Error-reading loop

```mermaid
flowchart LR
    Reproduce["1. Reproduce"] --> First["2. Find first useful 'Caused by'"]
    First --> Classify["3. Classify layer"]
    Classify --> Small["4. Reduce scope"]
    Small --> Hypothesis["5. Test one hypothesis"]
    Hypothesis --> Verify["6. Add regression test"]
```

Changing several unrelated things at once destroys evidence.

## Symptom map

| Symptom | Likely cause | First check |
|---|---|---|
| `mvn` not found | Maven absent from PATH | `mvn -version` |
| Unsupported class version | JDK mismatch | `java -version`, Maven Java home |
| Package does not exist | Dependency/import/package mismatch | `pom.xml`, Maven sync, package declaration |
| Port already in use | Another process owns `8080` | Change `server.port` or stop process |
| Bean not found | Class not scanned or no bean definition | Main package, annotation, constructor type |
| Circular dependency | A → B → A design | Draw constructor graph; extract responsibility |
| Failed DataSource | URL/driver/credentials unavailable | Effective profile and datasource properties |
| Table/column missing | Schema not migrated | Migration history and active DB |
| 404 | Wrong route/path or no controller mapping | Controller base path + method mapping |
| 405 | Correct path, wrong HTTP method | GET/POST/PUT/DELETE contract |
| 400 before controller | JSON binding or validation failed | Response body and DTO fields |
| 401 | No/invalid authentication | Credential and security chain |
| 403 | Identity lacks permission/CSRF issue | Authorities, ownership, client type |
| Lazy initialization error | Lazy data accessed after context closes | Query boundary and DTO mapping location |
| Too many SQL queries | N+1 relationship access | SQL logs and fetch plan |
| Transaction did not apply | Self-invocation/private method/wrong exception assumption | Proxy boundary and rollback rules |
| Test works alone only | Shared state/order dependency | Reset fixtures and remove ordering assumption |

## Bean failure

```mermaid
flowchart TD
    Fail["NoSuchBeanDefinition"] --> Type{"Is implementation managed?"}
    Type -- No --> Add["Use component annotation or @Bean"]
    Type -- Yes --> Scan{"Inside root package?"}
    Scan -- No --> Move["Move package or configure scan"]
    Scan -- Yes --> Many{"Multiple candidates?"}
    Many -- Yes --> Qualify["Use design, @Primary, or @Qualifier"]
    Many -- No --> Conditions["Check profile / conditional configuration"]
```

## HTTP failure

```mermaid
flowchart TD
    Req["Request fails"] --> Status{"Status"}
    Status -->|"404"| Mapping["Path + controller mapping"]
    Status -->|"405"| Method["HTTP method"]
    Status -->|"400"| Body["JSON shape + validation"]
    Status -->|"401/403"| Security["Authentication + authorization"]
    Status -->|"500"| Cause["Server log + first cause"]
```

## Database failure

```mermaid
flowchart LR
    Config["Effective JDBC config"] --> Connect["Can connect?"]
    Connect --> Schema["Correct schema version?"]
    Schema --> Mapping["Entity matches schema?"]
    Mapping --> Query["Generated SQL correct?"]
    Query --> Tx["Transaction commits?"]
```

## Useful commands

```bash
java -version
mvn -version
mvn clean verify
mvn spring-boot:run
mvn dependency:tree
curl -i http://localhost:8080/actuator/health
curl -i http://localhost:8080/api/tasks
```

## Ask for help with evidence

Provide:

```text
Expected behavior:
Actual behavior:
Smallest reproduction:
Exact command/request:
First meaningful exception and cause:
Java + Spring Boot versions:
Active profile:
Relevant controller/service/config:
What you already tested:
```
