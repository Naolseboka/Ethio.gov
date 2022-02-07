package et.gov.ethgov.land.application.mutation;

import et.gov.ethgov.land.domain.mutation.Mutation;
import et.gov.ethgov.land.domain.mutation.MutationId;
import et.gov.ethgov.land.domain.mutation.MutationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateMutationUseCase {
    private final MutationRepository repository;
    private final Clock clock;

    public ActivateMutationUseCase(MutationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public MutationId execute(ActivateMutationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        MutationId id = MutationId.parse(command.mutationId());
        Mutation aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("mutation not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateMutationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String mutationId,
            String reason) {}
}
