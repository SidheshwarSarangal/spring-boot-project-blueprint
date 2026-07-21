# Test this guide with first-time developers

[← Application selector](../README.md) · [Beginner feedback form](https://github.com/SidheshwarSarangal/spring-boot-project-blueprint/issues/new?template=beginner-feedback.yml)

Documentation is not proven beginner-friendly because its author can follow it. Use this protocol with people who have little or no Java/Spring Boot experience, record evidence, and improve the guide from repeated friction.

## Test objective

Determine whether a participant can independently:

1. install or verify the required toolchain;
2. choose the correct application type for a simple scenario;
3. build and start one unchanged example;
4. observe its documented result;
5. complete one small code change and automated test;
6. recover from one ordinary mistake using the troubleshooting material.

The objective is not to measure how quickly an expert can explain Spring.

## Participant profile

Recruit at least five participants over time. Record experience as categories, not personal details:

- never programmed;
- programmed, but not in Java;
- knows Java, but not Spring Boot;
- has tried Spring Boot but has not completed a project.

Include Windows, macOS, and Linux when possible. Do not publish names, recordings, terminal history, or repository contents without explicit consent.

## Session script

Give the participant only the repository URL and this goal:

> Set up the required tools, run the Taskboard API, create a task, and complete the first-project tutorial. Explain aloud what you expect before each command.

The observer may remind the participant to use repository navigation but should not provide technical answers. When assistance becomes necessary, record the exact point and the smallest hint supplied.

## Evidence to record

| Measure | Record |
|---|---|
| Setup success | Whether `java`, `javac`, Git, and Maven wrapper checks pass |
| Time to first build | Minutes from opening the README to `BUILD SUCCESS` |
| Time to first result | Minutes to the first successful health check and task creation |
| Navigation errors | Pages opened because the next action was unclear |
| Command errors | Command, operating system, and safe error excerpt |
| Vocabulary blockers | Unexplained term and page/heading where it appeared |
| Recovery | Whether troubleshooting solved the problem without a hint |
| Tutorial completion | Last checkpoint completed independently |
| Confidence | Participant rating from 1 (lost) to 5 (can repeat alone) |

Never record access tokens, passwords, private URLs, or other secrets.

## Observer notes template

```text
Session ID:
Date:
Experience category:
Operating system:
Editor/IDE:

First build: pass/fail, time
First observable result: pass/fail, time
Tutorial checkpoint reached:
Hints given:
Navigation dead ends:
Unclear words:
Errors not covered by troubleshooting:
Confidence before / after (1–5):
Most useful page:
One requested improvement:
```

## Decide what to change

Prioritize evidence using these rules:

1. Fix anything that blocks multiple participants.
2. Fix destructive, insecure, or misleading instructions immediately.
3. Prefer better navigation or one precise example over adding a long explanation.
4. Put operating-system-specific behavior in the setup guide.
5. Put repeatable failure recovery in troubleshooting.
6. Keep advanced alternatives out of the beginner's critical path.

For each documentation change, note which observed problem it addresses. Re-run the same task with a later participant; a change is validated only when the blocker no longer repeats.

## Publish evidence responsibly

Maintain an anonymized summary after conducting sessions:

```text
Sessions completed:
Operating systems represented:
Participants reaching first build without help:
Participants completing tutorial without technical hints:
Repeated blockers fixed:
Open repeated blockers:
Last test date:
```

Do not claim “tested with beginners” until sessions actually occurred. The repository's automated build results prove code health; usability sessions provide separate evidence about human comprehension.
