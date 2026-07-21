# Quality evidence and reproducible checks

[← Application selector](../README.md) · [CI workflow](../.github/workflows/verify.yml) · [Security workflow](../.github/workflows/codeql.yml) · [Usability testing](usability-testing.md)

This page separates claims that automation can prove from claims that require human evaluation. Use it when reviewing the repository or describing it publicly.

## Automated evidence

| Check | Runs when | What it establishes |
|---|---|---|
| Handbook validator | Pull requests and pushes to `main` | Local links resolve, code fences match, every process step has a location and completion check, and every starter has required source/test structure |
| Markdown lint | Pull requests and pushes to `main` | Markdown follows the repository's consistent structural rules |
| External-link check | Pull requests and pushes to `main` | Linked official references and resources are reachable at check time |
| Ten-project Maven matrix | Pull requests and pushes to `main` | Every application type compiles, tests, and packages independently on Java 17 |
| Dependency review | Pull requests | Newly introduced dependencies with known moderate-or-higher vulnerabilities fail the check |
| CodeQL | Pull requests, pushes to `main`, and weekly | Java source from every runnable project is compiled and statically analyzed for security-relevant patterns |
| Dependabot | Weekly | Maven dependencies and GitHub Actions receive update proposals |

Passing CI is evidence that the checked revision works under those conditions. It is not a guarantee that every deployment environment, external provider, or future dependency behaves identically.

## Reproduce the handbook checks locally

From the repository root:

```bash
./scripts/validate-handbook.sh
git diff --check
```

The external-link and Markdown-lint jobs use pinned major versions in GitHub Actions. Dependabot keeps those action references current.

## Reproduce every application build locally

Run the matching command inside each project folder:

```bash
./mvnw --batch-mode clean verify
```

Projects verified by the CI matrix:

1. Taskboard REST API
2. API gateway
3. Background worker
4. Batch application
5. Command-line application
6. Event-driven service
7. GraphQL API
8. Integration service
9. Real-time application
10. Server-rendered web application

The event-driven starter's default test/build mode does not require a running Kafka broker. Its README explains how to opt into a real local broker. External systems should additionally be tested with the same technology used in deployment.

## Human evidence

Automation cannot prove that a first-time developer understands a sentence, chooses the right path, or recovers confidently from a mistake. Use the [beginner usability protocol](usability-testing.md) and collect anonymized feedback through the repository issue form.

Public claims should remain precise:

- Say “all ten starters pass automated builds” when CI is green.
- Say “the guide provides a tested usability protocol” before sessions occur.
- Say “tested with first-time developers” only after conducting and summarizing real sessions.
- Report open repeated blockers alongside successful completion counts.

## Reviewer checklist

- Open the latest **Verify handbook and starters** workflow run.
- Confirm every matrix job is green, not only the handbook job.
- Open the latest CodeQL result and unresolved alerts.
- Inspect dependency-review results on dependency-changing pull requests.
- Run one starter and observe its documented output.
- Follow one application process step and confirm its location and completion check are unambiguous.
- Review usability evidence separately from automated evidence.
