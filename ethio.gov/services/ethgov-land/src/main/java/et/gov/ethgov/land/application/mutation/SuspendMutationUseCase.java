package et.gov.ethgov.land.application.mutation;

import et.gov.ethgov.land.domain.mutation.Mutation;
import et.gov.ethgov.land.domain.mutation.MutationId;
import et.gov.ethgov.land.domain.mutation.MutationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendMutationUseCase {
    private final MutationRepository repository;
    private final Clock clock;

    public SuspendMutationUseCase(MutationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public MutationId execute(SuspendMutationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        MutationId id = MutationId.parse(command.mutationId());
        Mutation aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("mutation not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendMutationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String mutationId,
            String reason) {}
}
