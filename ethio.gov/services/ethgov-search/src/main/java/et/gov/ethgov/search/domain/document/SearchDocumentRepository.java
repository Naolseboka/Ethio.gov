package et.gov.ethgov.search.domain.document;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SearchDocumentRepository {
    Optional<SearchDocument> findById(SearchDocumentId id);

    Optional<SearchDocument> findByTenantAndDocumentNumber(TenantId tenantId, DocumentNumber documentNumber);

    Optional<SearchDocument> findByTenantAndSource(TenantId tenantId, SourceType sourceType, SourceReference sourceId);

    List<SearchHit> search(
            TenantId tenantId, String query, Set<SourceType> sourceTypes, int limit);

    void save(SearchDocument document);
}
