# Capability: Caching

[← Application selector](../README.md) · [Production checklist](../docs/production-checklist.md)

Add caching only after measurement shows repeated computation/read latency is a problem. A cache is not the source of truth.

## 1. Define correctness

```text
Cached value and key:
Source of truth:
Acceptable staleness:
TTL:
Invalidation event:
Maximum size:
Behavior when cache is unavailable:
```

If acceptable staleness or invalidation cannot be stated, do not add the cache yet.

## 2. Choose scope

- In-memory cache: simplest, per application instance, lost on restart.
- Distributed cache such as Redis: shared across instances, adds network/operational failure modes.

Add Spring Cache and the selected provider. Enable caching in a dedicated configuration class, then apply cache behavior at service methods:

```java
@Cacheable(cacheNames = "tasks", key = "#id")
public TaskResponse findById(Long id) { /* load source of truth */ }

@CacheEvict(cacheNames = "tasks", key = "#id")
public TaskResponse update(Long id, UpdateTaskRequest request) { /* update */ }
```

Do not place `@EnableCaching` on the main application class; keep the capability replaceable in tests/environments.

## 3. Implement

1. Cache bounded read results with stable keys.
2. Set TTL and size/eviction limits.
3. Invalidate/update on every source-of-truth change.
4. Prevent cache keys from mixing users/tenants.
5. Avoid caching secrets or unbounded error responses.
6. Decide whether a cache failure falls back to the source or fails the request.
7. Measure hit rate, latency, evictions, and load on the source.

Typical files:

```text
src/main/java/com/company/project/cache/CacheConfiguration.java
src/main/java/com/company/project/feature/FeatureService.java
src/test/java/com/company/project/feature/FeatureCacheTest.java
```

Checkpoint: measure the uncached operation, prove miss → hit → invalidation, then measure again. Remove the cache if it does not provide a useful improvement.

## 4. Verify

Test miss, hit, expiry, update/delete invalidation, cross-user isolation, unavailable cache, concurrent misses, and stale-data limits.

Completion: correctness does not depend on cache availability, memory/key growth is bounded, invalidation is tested, and measurement shows useful benefit.
