# 05 · Database and JPA

[← Project workflow](00-project-workflow.md) · [Repository home](../README.md) · [API and data gate](00-project-workflow.md#gate-3--design-the-api-and-data)

## From Java to SQL

```mermaid
flowchart LR
    Entity["Task entity"] --> JPA["JPA mapping rules"]
    Repository["TaskRepository"] --> Hibernate["Hibernate"]
    JPA --> Hibernate
    Hibernate --> JDBC["JDBC driver"]
    JDBC --> DB[("SQL database")]
```

| Part | Job |
|---|---|
| JPA | Standard annotations and persistence interfaces |
| Hibernate | Common JPA implementation that tracks and writes entities |
| Spring Data JPA | Repository abstraction and query generation |
| JDBC driver | Database-specific wire protocol |
| Database | Durable source of truth |

## Entity ↔ table

```mermaid
erDiagram
    TASKS {
      BIGINT id PK
      VARCHAR title
      VARCHAR description
      VARCHAR status
      DATE due_date
      TIMESTAMP created_at
      TIMESTAMP updated_at
      BIGINT version
    }
```

```mermaid
flowchart LR
    ID["@Id + @GeneratedValue"] --> id["id"]
    COL["@Column"] --> columns["title / description"]
    ENUM["@Enumerated STRING"] --> status["status text"]
    VER["@Version"] --> version["optimistic lock"]
```

Prefer `EnumType.STRING`; ordinal numbers become dangerous when enum order changes.

## Entity lifecycle

```mermaid
stateDiagram-v2
    [*] --> Transient: new Task()
    Transient --> Managed: repository.save
    Managed --> Managed: fields change inside transaction
    Managed --> Detached: persistence context closes
    Managed --> Removed: repository.delete
    Removed --> [*]
```

Hibernate can detect changes to managed entities and write them at flush/commit. This is called dirty checking.

## Relationships

```mermaid
erDiagram
    PROJECT ||--o{ TASK : contains
    USER ||--o{ TASK : assigned
    TASK }o--o{ LABEL : tagged
```

| Relationship | Typical annotation | Use carefully because |
|---|---|---|
| Many tasks → one project | `@ManyToOne` | Loading and nullability must be deliberate |
| One project → many tasks | `@OneToMany` | Large collections and cascade rules can surprise |
| Many tasks ↔ many labels | `@ManyToMany` or join entity | A join entity is better when the relation has fields |

Start unidirectional. Add the reverse collection only when a use case needs navigation from that side.

## Lazy loading and N+1

```mermaid
sequenceDiagram
    participant App
    participant DB
    App->>DB: SELECT 20 tasks
    loop Each task
        App->>DB: SELECT its project
    end
    Note over App,DB: 1 + 20 queries = N+1
```

Use fetch joins, entity graphs, DTO projections, or purpose-built queries after confirming the access pattern. Do not make every relationship eager.

## Schema strategy

```mermaid
flowchart LR
    Dev["Disposable local development"] --> Update["ddl-auto: update<br/>convenient, not controlled"]
    Prod["Shared / production"] --> Migration["Flyway or Liquibase"]
    Migration --> V1["V1__create_tasks.sql"]
    V1 --> V2["V2__add_priority.sql"]
    V2 --> History[("schema history")]
```

Use one schema-generation mechanism. The Spring Boot documentation recommends avoiding a mix of basic SQL initialization with Flyway or Liquibase.

## Transactions

```mermaid
flowchart TD
    UseCase["Service use case"] --> Begin["BEGIN"]
    Begin --> Read["Read current state"]
    Read --> Decide["Apply rules"]
    Decide --> Write1["Write A"]
    Write1 --> Write2["Write B"]
    Write2 --> Outcome{"Success?"}
    Outcome -- Yes --> Commit["COMMIT"]
    Outcome -- No --> Rollback["ROLLBACK"]
```

Default Spring rollback behavior targets unchecked exceptions and errors. Be explicit when checked exceptions require rollback.

## Query choices

```mermaid
flowchart TD
    Need["Need data"] --> CRUD{"Standard CRUD?"}
    CRUD -- Yes --> Jpa["JpaRepository method"]
    CRUD -- No --> Simple{"Simple field condition?"}
    Simple -- Yes --> Derived["Derived method name"]
    Simple -- No --> EntityShape{"Need full entities?"}
    EntityShape -- Yes --> JPQL["JPQL / entity graph"]
    EntityShape -- No --> Projection["DTO projection / native SQL"]
```

Always paginate unbounded collections exposed by APIs.

## Local → production database

```mermaid
flowchart LR
    H2["H2 local development"] --> PG["PostgreSQL production"]
    PG --> Driver["PostgreSQL driver"]
    PG --> URL["JDBC URL from environment"]
    PG --> Migrate["Versioned migrations"]
    PG --> Pool["Connection pool limits"]
    PG --> Backup["Backups + recovery"]
```

H2 is useful for disposable local development, but database differences mean important integration tests should run against the same database engine used in production—often through Testcontainers.
