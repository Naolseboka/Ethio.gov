package et.gov.ethgov.document.application.documentset;

import et.gov.ethgov.document.domain.documentset.Documentset;
import et.gov.ethgov.document.domain.documentset.DocumentsetRepository;
import java.util.List;
import java.util.Objects;

public final class ListDocumentsetUseCase {
    private final DocumentsetRepository repository;

    public ListDocumentsetUseCase(DocumentsetRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Documentset> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
