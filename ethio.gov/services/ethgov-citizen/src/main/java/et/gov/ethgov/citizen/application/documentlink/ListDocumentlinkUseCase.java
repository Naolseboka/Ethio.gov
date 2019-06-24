package et.gov.ethgov.citizen.application.documentlink;

import et.gov.ethgov.citizen.domain.documentlink.Documentlink;
import et.gov.ethgov.citizen.domain.documentlink.DocumentlinkRepository;
import java.util.List;
import java.util.Objects;

public final class ListDocumentlinkUseCase {
    private final DocumentlinkRepository repository;

    public ListDocumentlinkUseCase(DocumentlinkRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Documentlink> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
