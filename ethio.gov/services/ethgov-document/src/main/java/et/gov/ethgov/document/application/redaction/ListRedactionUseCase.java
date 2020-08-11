package et.gov.ethgov.document.application.redaction;

import et.gov.ethgov.document.domain.redaction.Redaction;
import et.gov.ethgov.document.domain.redaction.RedactionRepository;
import java.util.List;
import java.util.Objects;

public final class ListRedactionUseCase {
    private final RedactionRepository repository;

    public ListRedactionUseCase(RedactionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Redaction> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
