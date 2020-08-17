package et.gov.ethgov.document.application.renderjob;

import et.gov.ethgov.document.domain.renderjob.Renderjob;
import et.gov.ethgov.document.domain.renderjob.RenderjobRepository;
import java.util.List;
import java.util.Objects;

public final class ListRenderjobUseCase {
    private final RenderjobRepository repository;

    public ListRenderjobUseCase(RenderjobRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Renderjob> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
