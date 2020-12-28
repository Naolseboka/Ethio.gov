package et.gov.ethgov.gateway.application.mtls;

import et.gov.ethgov.gateway.domain.mtls.Mtls;
import et.gov.ethgov.gateway.domain.mtls.MtlsRepository;
import java.util.List;
import java.util.Objects;

public final class ListMtlsUseCase {
    private final MtlsRepository repository;

    public ListMtlsUseCase(MtlsRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Mtls> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
