package et.gov.ethgov.land.application.mutation;

import et.gov.ethgov.land.domain.mutation.Mutation;
import et.gov.ethgov.land.domain.mutation.MutationRepository;
import java.util.List;
import java.util.Objects;

public final class ListMutationUseCase {
    private final MutationRepository repository;

    public ListMutationUseCase(MutationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Mutation> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
