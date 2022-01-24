package et.gov.ethgov.land.application.cadastralmap;

import et.gov.ethgov.land.domain.cadastralmap.Cadastralmap;
import et.gov.ethgov.land.domain.cadastralmap.CadastralmapRepository;
import java.util.List;
import java.util.Objects;

public final class ListCadastralmapUseCase {
    private final CadastralmapRepository repository;

    public ListCadastralmapUseCase(CadastralmapRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Cadastralmap> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
