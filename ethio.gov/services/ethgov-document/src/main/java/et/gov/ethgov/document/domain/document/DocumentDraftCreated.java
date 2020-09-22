package et.gov.ethgov.document.domain.document;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record DocumentDraftCreated(
        DocumentId documentId,
        TenantId tenantId,
        ContentType contentType,
        StorageObjectKey storageObjectKey,
        Instant occurredAt)
        implements DocumentDomainEvent {}
