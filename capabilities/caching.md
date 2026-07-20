# Capability process: Add caching

[← Application selector](../README.md) · [Testing](../docs/testing-guide.md) · [Production](../docs/production-checklist.md)

Insert this only after measurement shows repeated read/computation latency is a problem. Cache is not the source of truth.

## Step 1 · Measure and define correctness

> 📍 Add a `Caching decision` section to `<project-root>/PROJECT.md`. Put the repeatable baseline test in `src/test/java/com/company/project/task/TaskPerformanceTest.java` or record the production metric/query used.

Record cached value/key, source of truth, acceptable staleness, TTL, invalidation event, maximum size, tenant/user isolation, and unavailable-cache behavior.

Before continuing, check: Baseline latency/load is measured and invalidation can be stated. Otherwise stop—do not add cache.

Continue to Step 2.

## Step 2 · Choose provider and enable caching

> 📍 Edit `<project-root>/pom.xml` and `src/main/resources/application.yml`. Create `src/main/java/com/company/project/cache/CacheConfiguration.java`.

Add Spring Cache plus Caffeine for per-instance cache or Redis for shared multi-instance cache. Keep enablement in dedicated configuration:

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

Before continuing, check: Application starts with declared cache and fails/tests visibly if an undeclared cache name is used.

Continue to Step 3.

## Step 3 · Add read and invalidation at service boundary

> 📍 Edit `src/main/java/com/company/project/task/TaskService.java`. Create `src/test/java/com/company/project/task/TaskCacheTest.java`.

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

Before continuing, check: First read loads source, second hits cache, update/delete invalidates, next read loads fresh source.

Continue to Step 4.

## Step 4 · Test failure and prove benefit

> 📍 Add failure/expiry cases to `src/test/java/com/company/project/task/TaskCacheTest.java`, expose only approved metrics through `src/main/resources/application.yml`, and run commands in `<project-root>/`.

Test miss, hit, expiry, invalidation, cross-user isolation, unavailable cache, concurrent misses, and stale limit. Measure hit rate, latency, evictions, memory/key growth, and source load.

```bash
./mvnw clean verify
```

Before continuing, check: Source remains correct when cache is unavailable; size is bounded; post-cache measurement is meaningfully better.

Keep the cache and return to the path, or remove it if benefit is not justified.
