# Capability process: Add security

[← Application selector](../README.md) · [Testing](../docs/testing-guide.md) · [Production](../docs/production-checklist.md)

Insert this process when the application must identify callers or protect actions/data.

## Step 1 · Define identity, permission, and ownership

**What:** Produce explicit allow/deny rules before security configuration.

**Where:** Add an `Identity and access` section under the current feature in `<project-root>/PROJECT.md`.

**Do now:** Record public operations, caller identity source, role/authority, record owner, cross-user rule, and sensitive data.

**Finish this step when:** Every protected feature has at least one allowed and one denied scenario.

**Go next:** Step 2.

## Step 2 · Choose authentication and add dependencies

**What:** Select one established authentication model.

**Where:** Edit `<project-root>/pom.xml` and `src/main/resources/application.yml`; configure the identity provider in its admin console; set client secrets in the terminal/IDE run configuration or deployment secret store.

**Do now:**

| Client | Add/use |
|---|---|
| Browser forms/pages | Spring Security + server session/OIDC; retain CSRF |
| SPA/mobile/API | Spring Security + OAuth2 Resource Server validating tokens |
| Internal service | Organization-approved OAuth2 or mTLS service identity |

Do not invent token formats/crypto. Use an established identity provider. If the application owns passwords, store only adaptive `PasswordEncoder` hashes.

**Finish this step when:** Dependencies resolve and required issuer/client settings are known without committing secret values.

**Go next:** Step 3.

## Step 3 · Create security configuration

**What:** Protect one operation and keep intended public endpoints open.

**Where:** Create these paths; replace `com/company/project` with the package selected in Initializr.

```text
src/main/java/com/company/project/security/
├── SecurityConfiguration.java
├── CurrentUser.java
└── AuthorizationService.java
```

**Do now:** Token API example:

```java
@Configuration
@EnableMethodSecurity
class SecurityConfiguration {
    @Bean
    SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable()) // only for stateless bearer-token API
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/tasks/**")
                    .hasAuthority("SCOPE_tasks.write")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
            .build();
    }
}
```

For browser sessions use form/OIDC login and do not disable CSRF. Configure CORS only for required origins/methods/headers.

**Finish this step when:** Health/public route works; protected route returns `401` without valid identity and succeeds with allowed identity.

**Go next:** Step 4.

## Step 4 · Enforce business permission and ownership

**What:** Prevent a valid user from accessing another user’s protected data.

**Where:** Put reusable checks in `src/main/java/com/company/project/security/AuthorizationService.java` and call them from `src/main/java/com/company/project/task/TaskService.java`; do not rely only on URL rules.

**Do now:**

```java
@Transactional(readOnly = true)
public TaskResponse findById(Long id, CurrentUser user) {
    Task task = repository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException(id));
    if (!task.isOwnedBy(user.id()) && !user.isAdmin()) {
        throw new AccessDeniedException("Task is not accessible");
    }
    return mapper.toResponse(task);
}
```

Return/translate `401` for missing/invalid authentication and `403` for insufficient permission. Consider `404` instead of revealing existence when the contract requires privacy.

**Finish this step when:** User A cannot read/change User B’s record even by changing URL/body identifiers.

**Go next:** Step 5.

## Step 5 · Protect secrets/data and test the boundary

**What:** Prove all allow/deny cases and remove credential leakage.

**Where:** Create `src/test/java/com/company/project/security/SecurityIntegrationTest.java`; inspect application logs; keep secret values in the terminal/IDE run configuration or deployment secret store.

**Do now:** Test public, unauthenticated, invalid/expired identity, forbidden role, allowed role, cross-owner, CSRF (browser), and CORS. Never log passwords, tokens, cookies, session IDs, keys, or sensitive bodies.

```bash
./mvnw clean verify
```

**Finish this step when:** All permission tests pass; responses/logs contain no credentials; secrets remain outside Git.

**Go next:** Return to the application path’s next step.
