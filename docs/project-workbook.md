# Project workbook

[← Application selector](../README.md) · [Core REST implementation](core-guide.md) · [Production checklist](production-checklist.md)

Copy this file into a new project as `PROJECT.md`. Keep it short and update it as decisions change. It is the link between the requirement, code, tests, and delivery.

## 1. Project result

```text
Project name:
Problem being solved:
Primary user or calling system:
Successful final outcome:
Deadline or important constraints:
Explicitly out of scope:
```

## 2. Application shape

Select one primary shape:

- [ ] REST API
- [ ] Server-rendered web application
- [ ] Background/scheduled worker

Select required capabilities:

- [ ] Relational database
- [ ] Validation
- [ ] Authentication and authorization
- [ ] External HTTP provider
- [ ] Queue or event broker
- [ ] File/object storage
- [ ] Health and metrics

```text
Selected Spring Initializr dependencies:
Reason for each dependency:
Java version:
Spring Boot version:
Database/provider/broker choices:
```

## 3. Users and access

| Role or caller | May do | Must not do |
|---|---|---|
| | | |

```text
Authentication model:
Record ownership rule:
Sensitive data:
Audit requirement:
```

## 4. Feature queue

Build in this order. Keep only one feature in progress.

| Order | User action/result | Status | Verification |
|---|---|---|---|
| 1 | | planned | |

Allowed status: `planned`, `in progress`, `verified`, `released`.

## 5. Feature sheet

Copy this section once for each feature.

```text
Feature name:
User/caller:
Action:
Why it matters:

Given:
When:
Then:

Trigger (HTTP/form/event/schedule):
Input fields and validation:
Success output/status:
Expected failures/statuses:
Data read:
Data written:
Ownership/permission rule:
External systems called:
Timeout/retry/idempotency rule:
```

### Data sketch

| Field/table | Type | Required | Unique/default | Client or server owned |
|---|---|---|---|---|
| | | | | |

### Files to change

- [ ] Migration
- [ ] Entity
- [ ] Repository/query
- [ ] Request DTO/form/event
- [ ] Response DTO/view/result
- [ ] Mapper
- [ ] Service
- [ ] Controller/listener/scheduler
- [ ] Error handler
- [ ] Configuration/adapter
- [ ] Tests
- [ ] Example request and documentation

### Verification matrix

| Case | Input/start state | Expected result | Test/manual command |
|---|---|---|---|
| Success | | | |
| Invalid input | | | |
| Missing state | | | |
| Forbidden | | | |
| Conflict/failure | | | |

Feature is complete only when its clean build passes and the feature queue status is `verified`.

## 6. Environment configuration

| Variable | Purpose | Required | Safe default | Secret |
|---|---|---|---|---|
| | | | | |

```text
Local profile/setup:
Test configuration:
Production configuration source:
Migration process:
```

Never place real secret values in this file.

## 7. Delivery record

```text
Build command:
Run command:
Test command:
Migration command/process:
Health endpoint:
Deployment target:
Rollback or roll-forward plan:
Backup/restore owner:
Known limitations:
```

Final checks:

- [ ] Every required feature is verified.
- [ ] Clean build passes.
- [ ] Fresh setup instructions were tested.
- [ ] Migrations work on empty and supported existing databases.
- [ ] Secrets and generated data are not tracked.
- [ ] Permissions and ownership tests pass.
- [ ] External failures and timeouts are handled.
- [ ] Health, logs, and metrics are available.
- [ ] Production checklist is complete.
