# Capability process: Add file storage

[← Application selector](../README.md) · [Security](security.md) · [Production](../docs/production-checklist.md)

Insert this process for uploads, downloads, documents, images, exports, or imports.

## Step 1 · Define file and access contract

**What:** Specify accepted content, limits, ownership, retention, and cleanup.

**Where:** Feature sheet in `PROJECT.md`.

**Do:** Record allowed type/content, max size, filename use, owner, scan rule, storage lifetime, download authorization, processing, and partial-failure cleanup.

**Verify:** Oversized/invalid/unauthorized/missing-file outcomes are written before upload code.

**Next:** Step 2.

## Step 2 · Choose durable storage and configure request limits

**What:** Select a storage service and reject excessive requests before reading them.

**Where:** Storage provider setup, `application.yml`, secret/environment configuration.

**Do:** Use object/durable file storage for shared/production use, not the application container filesystem. Store bytes under generated keys and metadata/owner/status in database.

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
```

**Verify:** Application starts; oversized multipart request is rejected at configured limit.

**Next:** Step 3.

## Step 3 · Create storage interface and adapter

**What:** Isolate provider/file implementation from business service.

**Where:**

```text
src/main/java/com/company/project/file/
├── FileStorage.java
├── ObjectStorageAdapter.java
├── StoredFile.java
├── StoredFileRepository.java
└── FileService.java
```

**Do:**

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

**Verify:** Store and retrieve one small file through interface/adapter using generated key.

**Next:** Step 4.

## Step 4 · Add HTTP/job entry and authorization

**What:** Permit only authorized upload/download and validate untrusted content.

**Where:** Controller/job entry, service ownership checks, scanner/validator.

**Do:**

```java
@PostMapping(path = "/api/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
ResponseEntity<FileResponse> upload(@RequestPart MultipartFile file,
                                    Authentication authentication) throws IOException {
    StoredFile stored = service.upload(CurrentUser.from(authentication), file);
    return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(stored));
}
```

Validate size, filename, declared/actual content as risk requires; authorize every upload/download; scan untrusted content; use short-lived signed URLs when direct transfer is selected.

**Verify:** Valid owner succeeds; invalid/oversized/malicious filename rejects; User A cannot download User B’s private file.

**Next:** Step 5.

## Step 5 · Handle partial failure and test storage outage

**What:** Keep metadata and stored bytes consistent/recoverable.

**Where:** Service compensation/reconciliation, cleanup job, tests, metrics.

**Do:** Delete orphaned bytes when metadata save fails or mark state for reconciliation; handle missing object; record safe storage outcome; test interrupted upload/outage. Do not put remote storage operation inside a database transaction and assume automatic rollback.

```bash
./mvnw clean verify
```

**Verify:** Partial upload and storage outage have deterministic cleanup/recovery; no path traversal/unbounded memory/access leak.

**Next:** Return to the application path’s next step.
