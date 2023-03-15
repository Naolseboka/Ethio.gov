package et.gov.ethgov.opendata.application.download;

import et.gov.ethgov.opendata.domain.download.Download;
import et.gov.ethgov.opendata.domain.download.DownloadRepository;
import java.util.List;
import java.util.Objects;

public final class ListDownloadUseCase {
    private final DownloadRepository repository;

    public ListDownloadUseCase(DownloadRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Download> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
