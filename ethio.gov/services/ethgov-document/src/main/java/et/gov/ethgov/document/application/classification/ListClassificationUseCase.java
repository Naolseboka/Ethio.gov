package et.gov.ethgov.document.application.classification;

import et.gov.ethgov.document.domain.classification.Classification;
import et.gov.ethgov.document.domain.classification.ClassificationRepository;
import java.util.List;
import java.util.Objects;

public final class ListClassificationUseCase {
    private final ClassificationRepository repository;

    public ListClassificationUseCase(ClassificationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Classification> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
