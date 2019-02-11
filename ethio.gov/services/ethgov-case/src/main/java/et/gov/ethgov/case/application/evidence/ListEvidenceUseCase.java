package et.gov.ethgov.case.application.evidence;

import et.gov.ethgov.case.domain.evidence.Evidence;
import et.gov.ethgov.case.domain.evidence.EvidenceRepository;
import java.util.List;
import java.util.Objects;

public final class ListEvidenceUseCase {
    private final EvidenceRepository repository;

    public ListEvidenceUseCase(EvidenceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Evidence> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
