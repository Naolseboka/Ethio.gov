package et.gov.ethgov.licensing.application.endorsement;

import et.gov.ethgov.licensing.domain.endorsement.Endorsement;
import et.gov.ethgov.licensing.domain.endorsement.EndorsementRepository;
import java.util.List;
import java.util.Objects;

public final class ListEndorsementUseCase {
    private final EndorsementRepository repository;

    public ListEndorsementUseCase(EndorsementRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Endorsement> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
