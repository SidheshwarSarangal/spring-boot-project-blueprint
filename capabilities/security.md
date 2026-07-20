# Capability process: Add security

[← Application selector](../README.md) · [Testing](../docs/testing-guide.md) · [Production](../docs/production-checklist.md)

Insert this process when the application must identify callers or protect actions/data.

## Repository action map

| Step | Exact location | Add or run there |
|---|---|---|
| 1 | Edit users/access + feature in `<project-root>/PROJECT.md` | Identity/role/ownership rules |
| 2 | Edit `<project-root>/pom.xml` and external identity-provider settings | Security/resource-server/OIDC dependencies |
| 3 | Create `src/main/java/com/company/project/security/SecurityConfiguration.java` and related types | Filter-chain/authentication code |
| 4 | Edit protected feature service/authorization service | Record ownership/business permission |
| 5 | Create `src/test/java/com/company/project/security/`; edit logs/secrets config | Allow/deny/leakage tests |

## Step 1 · Define identity, permission, and ownership

**What:** Produce explicit allow/deny rules before security configuration.

**Where:** Users/access section and feature sheet in `PROJECT.md`.

**Do:** Record public operations, caller identity source, role/authority, record owner, cross-user rule, and sensitive data.

**Verify:** Every protected feature has at least one allowed and one denied scenario.

**Next:** Step 2.

## Step 2 · Choose authentication and add dependencies

**What:** Select one established authentication model.

**Where:** Spring Initializr/`pom.xml` and identity-provider configuration.

**Do:**

| Client | Add/use |
|---|---|
| Browser forms/pages | Spring Security + server session/OIDC; retain CSRF |
| SPA/mobile/API | Spring Security + OAuth2 Resource Server validating tokens |
| Internal service | Organization-approved OAuth2 or mTLS service identity |

Do not invent token formats/crypto. Use an established identity provider. If the application owns passwords, store only adaptive `PasswordEncoder` hashes.

**Verify:** Dependencies resolve and required issuer/client settings are known without committing secret values.

**Next:** Step 3.

## Step 3 · Create security configuration

**What:** Protect one operation and keep intended public endpoints open.

**Where:**

```text
src/main/java/com/company/project/security/
├── SecurityConfiguration.java
├── CurrentUser.java
└── AuthorizationService.java
```

**Do:** Token API example:

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

**Verify:** Health/public route works; protected route returns `401` without valid identity and succeeds with allowed identity.

**Next:** Step 4.

## Step 4 · Enforce business permission and ownership

**What:** Prevent a valid user from accessing another user’s protected data.

**Where:** Feature service or application authorization service—not only URL configuration.

**Do:**

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

**Verify:** User A cannot read/change User B’s record even by changing URL/body identifiers.

**Next:** Step 5.

## Step 5 · Protect secrets/data and test the boundary

**What:** Prove all allow/deny cases and remove credential leakage.

**Where:** Security tests, logs/error handling, secret/configuration store.

**Do:** Test public, unauthenticated, invalid/expired identity, forbidden role, allowed role, cross-owner, CSRF (browser), and CORS. Never log passwords, tokens, cookies, session IDs, keys, or sensitive bodies.

```bash
./mvnw clean verify
```

**Verify:** All permission tests pass; responses/logs contain no credentials; secrets remain outside Git.

**Next:** Return to the application path’s next step.
