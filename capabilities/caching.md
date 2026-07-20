# Capability process: Add caching

[← Application selector](../README.md) · [Testing](../docs/testing-guide.md) · [Production](../docs/production-checklist.md)

Insert this only after measurement shows repeated read/computation latency is a problem. Cache is not the source of truth.

## Step 1 · Measure and define correctness

**What:** Prove a cache is needed and specify safe staleness/invalidation.

**Where:** Feature performance notes in `PROJECT.md` and baseline metric/test.

**Do:** Record cached value/key, source of truth, acceptable staleness, TTL, invalidation event, maximum size, tenant/user isolation, and unavailable-cache behavior.

**Verify:** Baseline latency/load is measured and invalidation can be stated. Otherwise stop—do not add cache.

**Next:** Step 2.

## Step 2 · Choose provider and enable caching

**What:** Configure one bounded local/distributed cache.

**Where:** `pom.xml`, `CacheConfiguration.java`, `application.yml`.

**Do:** Add Spring Cache plus Caffeine for per-instance cache or Redis for shared multi-instance cache. Keep enablement in dedicated configuration:

```java
@Configuration
@EnableCaching
class CacheConfiguration {}
```

```yaml
spring:
  cache:
    cache-names: tasks
    caffeine:
      spec: maximumSize=500,expireAfterWrite=10m
```

Use provider-specific properties matching the selected provider. Do not place `@EnableCaching` on the main application class.

**Verify:** Application starts with declared cache and fails/tests visibly if an undeclared cache name is used.

**Next:** Step 3.

## Step 3 · Add read and invalidation at service boundary

**What:** Cache one read and evict/update it with every source-of-truth change.

**Where:** Feature service and cache-specific test.

**Do:**

```java
@Cacheable(cacheNames = "tasks", key = "#id")
public TaskResponse findById(Long id) {
    return mapper.toResponse(repository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException(id)));
}

@CacheEvict(cacheNames = "tasks", key = "#id")
@Transactional
public TaskResponse update(Long id, UpdateTaskRequest request) {
    return mapper.toResponse(updateSourceOfTruth(id, request));
}
```

Include tenant/user identity in keys where data is scoped. Avoid self-invocation that bypasses Spring caching proxies.

**Verify:** First read loads source, second hits cache, update/delete invalidates, next read loads fresh source.

**Next:** Step 4.

## Step 4 · Test failure and prove benefit

**What:** Ensure correctness does not depend on cache and benefit is measurable.

**Where:** Cache integration tests and metrics.

**Do:** Test miss, hit, expiry, invalidation, cross-user isolation, unavailable cache, concurrent misses, and stale limit. Measure hit rate, latency, evictions, memory/key growth, and source load.

```bash
./mvnw clean verify
```

**Verify:** Source remains correct when cache is unavailable; size is bounded; post-cache measurement is meaningfully better.

**Next:** Keep the cache and return to the path, or remove it if benefit is not justified.
