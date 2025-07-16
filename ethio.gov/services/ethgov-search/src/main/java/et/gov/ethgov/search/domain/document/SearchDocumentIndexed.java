package et.gov.ethgov.search.domain.document;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.time.Instant;

public record SearchDocumentIndexed(
        SearchDocumentId searchDocumentId,
        TenantId tenantId,
        DocumentNumber documentNumber,
        SourceType sourceType,
        SourceReference sourceId,
        Instant occurredAt)
        implements SearchDocumentDomainEvent {}
