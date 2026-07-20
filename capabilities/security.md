# Capability: Security

[← Application selector](../README.md) · [Production checklist](../docs/production-checklist.md)

Add this when the application must identify callers or protect an action/data.

## 1. Define the rule first

```text
Who is the caller?
How is identity established?
Which role/permission is required?
Does the record have an owner?
What is public?
```

## 2. Choose the model

| Client | Common choice |
|---|---|
| Browser pages/forms | Server session, secure cookie, CSRF protection |
| SPA/mobile/HTTP service | OAuth 2.0/OIDC resource server validating access tokens |
| Internal service | Organization-approved service identity, often OAuth 2.0 or mTLS |

Use Spring Security and an established identity provider where possible. Do not invent token formats, encryption, or password storage.

## 3. Implement in layers

```text
authentication → route-level authorization → service ownership rule
→ safe response/logging → security tests
```

1. Configure public and protected entry points.
2. Validate identity using the selected provider/session mechanism.
3. Enforce broad permissions at the web/method boundary.
4. Enforce record ownership and business permissions in the service.
5. Return `401` for missing/invalid authentication and `403` for insufficient permission.
6. Retain CSRF protection for browser session applications.
7. Restrict CORS to required origins, methods, and headers.

## 4. Protect data

Never log passwords, tokens, session IDs, secrets, or sensitive payloads. Store application-owned passwords only through an approved adaptive `PasswordEncoder`. Keep keys and client secrets in a secret manager.

## 5. Verify

Test public access, unauthenticated access, invalid identity, forbidden role, allowed role, cross-user ownership, expired credentials, CSRF where relevant, and sensitive log/response leakage.

Completion: every protected operation has allowed and denied tests, ownership cannot be bypassed, and credentials remain outside source/logs.
