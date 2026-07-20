# Capability: Data storage

[← Application selector](../README.md) · [Troubleshooting](../docs/troubleshooting.md)

Add this only when application state must survive process restart.

## 1. Choose one primary data model

| Requirement | Choose |
|---|---|
| Transactions, relationships, constraints, reporting | SQL database + Spring Data JPA |
| Document-shaped data with document access patterns | MongoDB + Spring Data MongoDB |
| Temporary key/value state | Usually cache/session storage; do not treat it as the source of truth without a clear design |

Prefer SQL unless the requirement clearly fits another model.

## 2. Add dependencies

For SQL, add Spring Data JPA, the chosen database driver, and Flyway or Liquibase. For MongoDB, add Spring Data MongoDB.

Use H2 only for disposable local examples. Important integration tests should use the same database engine as production.

## 3. Design before mapping

For every stored field decide type, nullability, length, uniqueness, default, ownership, indexes, and relationships. For growing data, define pagination and retention.

## 4. Implement

SQL order:

```text
migration → entity → repository/query → service transaction → mapper/DTO → test
```

MongoDB order:

```text
document/index design → document class → repository/query
→ service operation → mapper/DTO → test
```

Keep transactions around complete service use cases. Do not expose persistence objects directly as public API responses.

## 5. Configure

Supply production connection URL, user, and password outside Git. Validate required settings at startup. Configure connection-pool/query timeouts and disable schema auto-update in shared environments.

## 6. Verify

Test constraints, mappings, custom queries, pagination, concurrent updates where relevant, migrations from supported prior versions, and failure rollback.

Completion: schema changes are repeatable, queries are bounded, service operations have correct transaction behavior, and backup/restore ownership is known.
