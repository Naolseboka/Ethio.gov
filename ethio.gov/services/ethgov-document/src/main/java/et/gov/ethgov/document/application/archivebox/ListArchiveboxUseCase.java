package et.gov.ethgov.document.application.archivebox;

import et.gov.ethgov.document.domain.archivebox.Archivebox;
import et.gov.ethgov.document.domain.archivebox.ArchiveboxRepository;
import java.util.List;
import java.util.Objects;

public final class ListArchiveboxUseCase {
    private final ArchiveboxRepository repository;

    public ListArchiveboxUseCase(ArchiveboxRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Archivebox> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
