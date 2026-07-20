# Capability: File storage

[← Application selector](../README.md) · [Production checklist](../docs/production-checklist.md)

Add this for uploads, downloads, documents, images, exports, or imports.

## 1. Define the file contract

Record allowed content, maximum size, ownership, retention, download authorization, processing, and failure cleanup.

## 2. Choose storage

Use object storage or another durable file service for shared/production use. Do not rely on the application container filesystem. Store file metadata, ownership, state, and storage key in the database—not necessarily the bytes.

For HTTP uploads, Spring Web already provides multipart handling. Set explicit request/file limits in configuration; do not rely on defaults:

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
```

## 3. Implement safely

```text
request/job → validate → generate storage key → stream bytes
→ store metadata → authorized download/processing
```

1. Never use a client filename as a filesystem/storage path.
2. Validate size, filename, declared type, and actual content as risk requires.
3. Stream large data instead of loading it fully into memory.
4. Authorize every upload and download.
5. Scan untrusted content when appropriate.
6. Use short-lived signed URLs for direct object-storage transfer when useful.
7. Clean up partial uploads and reconcile bytes/metadata after failure.

Typical files:

```text
src/main/java/com/company/project/file/
├── FileStorage.java            application-owned interface
├── ObjectStorageAdapter.java
├── StoredFile.java             metadata/ownership entity
├── FileService.java
└── FileController.java         only for HTTP upload/download
```

Checkpoint: store and retrieve one small test file, reject one oversized/invalid file, and prove cross-user access is denied before enabling large uploads.

## 4. Verify

Test valid file, empty/oversized file, invalid content, malicious filename, unauthorized/cross-user download, interrupted upload, missing object, and storage outage.

Completion: untrusted input cannot control paths or unbounded memory, access is authorized, storage is durable, and partial failure has cleanup/recovery.
