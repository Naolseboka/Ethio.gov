package et.gov.ethgov.document.application.storage;

import et.gov.ethgov.document.domain.document.ContentType;
import et.gov.ethgov.document.domain.document.StorageObjectKey;
import java.net.URI;
import java.time.Duration;
import java.util.Optional;

/** Outbound port for S3-compatible object storage (MinIO in local/dev). */
public interface ObjectStoragePort {

    URI createUploadUrl(StorageObjectKey key, ContentType contentType, long contentLength, Duration ttl);

    URI createDownloadUrl(StorageObjectKey key, Duration ttl);

    Optional<StoredObjectMetadata> head(StorageObjectKey key);

    record StoredObjectMetadata(long contentLength, String contentType, String eTag) {}
}
