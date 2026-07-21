# Contributing

Thank you for helping make Spring Boot project creation more approachable. Contributions should preserve the repository's core promise: a beginner can choose one path, complete one observable feature, verify it, and add only the capabilities that feature requires.

## Report beginner friction

Use the [beginner learning feedback form](https://github.com/SidheshwarSarangal/spring-boot-project-blueprint/issues/new?template=beginner-feedback.yml) for unclear instructions, navigation dead ends, missing setup details, or troubleshooting gaps. Remove secrets and proprietary information before submitting.

## Documentation changes

- Prefer a precise location, action, and expected result over general advice.
- Define an unfamiliar term at first use or link to the foundation/primer.
- Keep operating-system-specific setup in `docs/setup-guide.md`.
- Keep advanced alternatives outside the beginner critical path.
- Link to primary official documentation for changing technical facts.
- Do not claim human usability testing without recorded anonymized sessions.

Run:

```bash
./scripts/validate-handbook.sh
git diff --check
```

## Starter changes

Keep each starter independent and minimal. Do not combine application types or add a capability merely to demonstrate it. Update its README, tests, source walkthrough, and relevant application path together.

Inside the changed project run:

```bash
./mvnw --batch-mode clean verify
```

If a shared convention or dependency changes, run all ten projects as described in [`docs/quality-evidence.md`](docs/quality-evidence.md).

## Pull request evidence

Describe:

- the learner or project problem being solved;
- files and paths affected;
- commands and observable checks performed;
- screenshots only when visual behavior materially changed;
- remaining limitations or follow-up work.

All required CI jobs must pass. A green build does not override unclear documentation, and positive prose review does not override a failing starter.
