# Capability process: Add file storage

[← Application selector](../README.md) · [Security](security.md) · [Production](../docs/production-checklist.md)

Insert this process for uploads, downloads, documents, images, exports, or imports.

## Repository action map

| Step | Exact location | Add or run there |
|---|---|---|
| 1 | Edit feature sheet in `<project-root>/PROJECT.md` | Content/size/access/retention contract |
| 2 | Edit `src/main/resources/application.yml` and storage provider environment | Multipart/storage settings |
| 3 | Create `src/main/java/com/company/project/file/` | Storage interface/adapter/entity/service code |
| 4 | Edit/create `file/FileController.java` or selected job entry; edit security/service | Upload/download/authorization code |
| 5 | Create matching `src/test/java/.../file/`; create cleanup/reconciliation job if required | Failure/outage/cleanup proof |

**Beginner actions by step:** 1 → [A workbook](../docs/beginner-execution-guide.md#action-a-create-the-working-repository-and-workbook); 2 → [H multipart/storage YAML](../docs/beginner-execution-guide.md#action-h-edit-yaml-configuration); 3–4 → [E create file package](../docs/beginner-execution-guide.md#action-e-create-a-java-package-and-file), [F add code](../docs/beginner-execution-guide.md#action-f-put-a-provided-java-code-block-into-a-file), [J upload call](../docs/beginner-execution-guide.md#action-j-start-the-application-and-call-it); 5 → [K tests](../docs/beginner-execution-guide.md#action-k-create-and-run-a-test), [M checkpoint](../docs/beginner-execution-guide.md#action-m-save-a-clean-checkpoint-with-git).

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
