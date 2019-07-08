package et.gov.ethgov.citizen.application.namechange;

import et.gov.ethgov.citizen.domain.namechange.Namechange;
import et.gov.ethgov.citizen.domain.namechange.NamechangeRepository;
import java.util.List;
import java.util.Objects;

public final class ListNamechangeUseCase {
    private final NamechangeRepository repository;

    public ListNamechangeUseCase(NamechangeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Namechange> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
