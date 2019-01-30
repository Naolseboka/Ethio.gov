package et.gov.ethgov.case.application.casenote;

import et.gov.ethgov.case.domain.casenote.Casenote;
import et.gov.ethgov.case.domain.casenote.CasenoteRepository;
import java.util.List;
import java.util.Objects;

public final class ListCasenoteUseCase {
    private final CasenoteRepository repository;

    public ListCasenoteUseCase(CasenoteRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Casenote> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
