package et.gov.ethgov.land.application.mutation;

import et.gov.ethgov.land.domain.mutation.Mutation;
import et.gov.ethgov.land.domain.mutation.MutationId;
import et.gov.ethgov.land.domain.mutation.MutationRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateMutationUseCase {
    private final MutationRepository repository;
    private final Clock clock;

    public CreateMutationUseCase(MutationRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public MutationId execute(CreateMutationCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Mutation aggregate = Mutation.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateMutationCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String mutationId,
            String reason) {}
}
