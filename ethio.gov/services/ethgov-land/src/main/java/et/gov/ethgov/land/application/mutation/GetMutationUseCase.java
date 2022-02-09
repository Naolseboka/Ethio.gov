package et.gov.ethgov.land.application.mutation;

import et.gov.ethgov.land.domain.mutation.Mutation;
import et.gov.ethgov.land.domain.mutation.MutationId;
import et.gov.ethgov.land.domain.mutation.MutationRepository;
import java.util.Objects;

public final class GetMutationUseCase {
    private final MutationRepository repository;

    public GetMutationUseCase(MutationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Mutation execute(String rawId) {
        MutationId id = MutationId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("mutation not found: " + rawId));
    }
}
