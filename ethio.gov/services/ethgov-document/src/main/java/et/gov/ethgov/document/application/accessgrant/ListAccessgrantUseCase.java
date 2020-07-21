package et.gov.ethgov.document.application.accessgrant;

import et.gov.ethgov.document.domain.accessgrant.Accessgrant;
import et.gov.ethgov.document.domain.accessgrant.AccessgrantRepository;
import java.util.List;
import java.util.Objects;

public final class ListAccessgrantUseCase {
    private final AccessgrantRepository repository;

    public ListAccessgrantUseCase(AccessgrantRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Accessgrant> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
