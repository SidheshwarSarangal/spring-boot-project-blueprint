# Capability process: Add file storage

[← Application selector](../README.md) · [Security](security.md) · [Production](../docs/production-checklist.md)

Insert this process for uploads, downloads, documents, images, exports, or imports.

## Step 1 · Define file and access contract

**What:** Specify accepted content, limits, ownership, retention, and cleanup.

**Where:** Add a `File contract` section under the current feature in `<project-root>/PROJECT.md`.

**Do now:** Record allowed type/content, max size, filename use, owner, scan rule, storage lifetime, download authorization, processing, and partial-failure cleanup.

**Finish this step when:** Oversized/invalid/unauthorized/missing-file outcomes are written before upload code.

**Go next:** Step 2.

## Step 2 · Choose durable storage and configure request limits

**What:** Select a storage service and reject excessive requests before reading them.

**Where:** Configure the provider outside the repository, edit `src/main/resources/application.yml`, and set credentials in the terminal/IDE run configuration or deployment secret store.

**Do now:** Use object/durable file storage for shared/production use, not the application container filesystem. Store bytes under generated keys and metadata/owner/status in database.

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
```

**Finish this step when:** Application starts; oversized multipart request is rejected at configured limit.

**Go next:** Step 3.

## Step 3 · Create storage interface and adapter

**What:** Isolate provider/file implementation from business service.

**Where:** Create these paths; replace `com/company/project` with the package selected in Initializr.

```text
src/main/java/com/company/project/file/
├── FileStorage.java
├── ObjectStorageAdapter.java
├── StoredFile.java
├── StoredFileRepository.java
└── FileService.java
```

**Do now:**

```java
public interface FileStorage {
    void put(String key, InputStream content, long size, String contentType);
    InputStream get(String key);
    void delete(String key);
}

@Service
class FileService {
    private final FileStorage storage;
    private final StoredFileRepository files;

    StoredFile upload(CurrentUser user, MultipartFile file) throws IOException {
        validate(file);
        String key = UUID.randomUUID().toString();
        storage.put(key, file.getInputStream(), file.getSize(), file.getContentType());
        return files.save(StoredFile.uploaded(key, file.getOriginalFilename(), user.id()));
    }
}
```

Never use client filename as storage/filesystem path. Stream content; do not call `getBytes()` for large files.

**Finish this step when:** Store and retrieve one small file through interface/adapter using generated key.

**Go next:** Step 4.

## Step 4 · Add HTTP/job entry and authorization

**What:** Permit only authorized upload/download and validate untrusted content.

**Where:** Create `src/main/java/com/company/project/file/FileController.java` (or `FileJob.java`) and `FileValidator.java`; keep ownership checks in `FileService.java`.

**Do now:**

```java
@PostMapping(path = "/api/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
ResponseEntity<FileResponse> upload(@RequestPart MultipartFile file,
                                    Authentication authentication) throws IOException {
    StoredFile stored = service.upload(CurrentUser.from(authentication), file);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(stored));
}
```

Validate size, filename, declared/actual content as risk requires; authorize every upload/download; scan untrusted content; use short-lived signed URLs when direct transfer is selected.

**Finish this step when:** Valid owner succeeds; invalid/oversized/malicious filename rejects; User A cannot download User B’s private file.

**Go next:** Step 5.

## Step 5 · Handle partial failure and test storage outage

**What:** Keep metadata and stored bytes consistent/recoverable.

**Where:** Edit `src/main/java/com/company/project/file/FileService.java`; create `FileCleanupJob.java` and `src/test/java/com/company/project/file/FileStorageIntegrationTest.java`; configure metrics in `src/main/resources/application.yml`.

**Do now:** Delete orphaned bytes when metadata save fails or mark state for reconciliation; handle missing object; record safe storage outcome; test interrupted upload/outage. Do not put remote storage operation inside a database transaction and assume automatic rollback.

```bash
./mvnw clean verify
```

**Finish this step when:** Partial upload and storage outage have deterministic cleanup/recovery; no path traversal/unbounded memory/access leak.

**Go next:** Return to the application path’s next step.
