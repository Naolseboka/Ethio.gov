package et.gov.ethgov.identity.application.recovery;

import et.gov.ethgov.identity.domain.recovery.Recovery;
import et.gov.ethgov.identity.domain.recovery.RecoveryRepository;
import java.util.List;
import java.util.Objects;

public final class ListRecoveryUseCase {
    private final RecoveryRepository repository;

    public ListRecoveryUseCase(RecoveryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Recovery> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
