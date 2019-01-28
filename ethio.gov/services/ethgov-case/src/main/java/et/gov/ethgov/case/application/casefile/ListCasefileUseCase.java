package et.gov.ethgov.case.application.casefile;

import et.gov.ethgov.case.domain.casefile.Casefile;
import et.gov.ethgov.case.domain.casefile.CasefileRepository;
import java.util.List;
import java.util.Objects;

public final class ListCasefileUseCase {
    private final CasefileRepository repository;

    public ListCasefileUseCase(CasefileRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Casefile> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
