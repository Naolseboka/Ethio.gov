package et.gov.ethgov.opendata.domain.download;

import java.util.List;
import java.util.Optional;

public interface DownloadRepository {
    Download save(Download aggregate);

    Optional<Download> findById(DownloadId id);

    Optional<Download> findByReferenceCode(String tenantId, String referenceCode);

    List<Download> findByTenant(String tenantId, int limit);

    void delete(DownloadId id);
}
