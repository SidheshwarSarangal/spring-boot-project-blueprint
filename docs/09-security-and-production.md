# 09 · Security and production

[← Project workflow](00-project-workflow.md) · [Repository home](../README.md) · [Environment gate](00-project-workflow.md#gate-9--prepare-shared-environments)

Adding Spring Security changes the request path before the controller.

```mermaid
flowchart LR
    Client["Client + credentials"] --> Chain["SecurityFilterChain"]
    Chain --> AuthN{"Authenticated?"}
    AuthN -- No --> U["401"]
    AuthN -- Yes --> AuthZ{"Authorized?"}
    AuthZ -- No --> F["403"]
    AuthZ -- Yes --> Controller["Controller"]
```

## Authentication vs authorization

| Question | Concept | Example |
|---|---|---|
| Who are you? | Authentication | Verify session, bearer token, or password |
| May you do this? | Authorization | User may edit only their own task |

```mermaid
flowchart LR
    Credential["Credential"] --> Provider["AuthenticationProvider"]
    Provider --> Identity["Authentication"]
    Identity --> Context["SecurityContext"]
    Context --> Decision["AuthorizationManager"]
    Decision --> Resource["HTTP route / service method"]
```

Spring Security is filter-based for servlet applications. Roles and authorities are read from the authenticated identity when authorization decisions are made.

## Pick an authentication model

```mermaid
flowchart TD
    Client{"Client type"}
    Client -->|"Server-rendered browser app"| Session["Session + form/OIDC login"]
    Client -->|"First-party SPA/mobile API"| OIDC["OIDC/OAuth2 design"]
    Client -->|"Service-to-service"| Token["OAuth2 resource server / mTLS"]
    Client -->|"Small internal prototype"| Basic["HTTP Basic over TLS<br/>limited use"]
```

Do not invent a custom password or token protocol. Store passwords through a `PasswordEncoder`, never as plaintext.

## Security checklist by boundary

```mermaid
flowchart TB
    S["Security"]
    S --> Input["Input<br/>validate type, size, content"]
    S --> Identity["Identity<br/>trusted authentication"]
    S --> Access["Access<br/>resource ownership + roles"]
    S --> Data["Data<br/>TLS, encryption, minimization"]
    S --> Secrets["Secrets<br/>runtime manager"]
    S --> Supply["Supply chain<br/>updates + scanning"]
    S --> Ops["Operations<br/>logs, alerts, backups"]
```

## Production runtime

```mermaid
flowchart LR
    User --> Proxy["TLS ingress / reverse proxy"]
    Proxy --> App["Spring Boot instances"]
    App --> DB[("Managed database")]
    App --> Cache[("Cache if needed")]
    App --> Queue[("Queue if needed")]
    App --> Logs["Central logs"]
    App --> Metrics["Metrics + alerts"]
    App --> Traces["Distributed traces"]
```

## Actuator

```mermaid
flowchart LR
    App["Application"] --> Health["/actuator/health"]
    App --> Info["/actuator/info"]
    App --> Metrics["Micrometer metrics"]
    Health --> Platform["Readiness / monitoring"]
    Metrics --> Backend["Monitoring backend"]
```

Expose only required endpoints. Health is conventionally available at `/actuator/health`; detailed health, configuration, environment, mappings, and heap data may reveal sensitive information and need protection.

## Deployment pipeline

```mermaid
flowchart LR
    Commit["Commit"] --> CI["Compile + tests + scans"]
    CI --> Jar["Versioned JAR/image"]
    Jar --> Migrate["Controlled DB migration"]
    Migrate --> Deploy["Deploy"]
    Deploy --> Ready["Readiness check"]
    Ready --> Traffic["Receive traffic"]
    Traffic --> Observe["Logs + metrics + alerts"]
    Observe --> Rollback["Rollback plan"]
```

## Before production

- [ ] Authentication mechanism matches the client and threat model.
- [ ] Every resource checks authorization/ownership.
- [ ] TLS is enforced.
- [ ] Secrets come from a secret manager or platform.
- [ ] Uploads, URLs, and serialized input have limits.
- [ ] Database migrations are reviewed and reversible where practical.
- [ ] Timeouts, connection pools, and retry limits are explicit.
- [ ] Health/readiness checks match real dependencies.
- [ ] Logs are structured and free of secrets/personal data.
- [ ] Metrics and alerts cover latency, errors, saturation, and business failures.
- [ ] Backups and restore procedures are tested.
- [ ] Dependency and container scanning runs in CI.
- [ ] Rollback or roll-forward procedure exists.
