package et.gov.ethgov.organization.application.seal;

import et.gov.ethgov.organization.domain.seal.Seal;
import et.gov.ethgov.organization.domain.seal.SealId;
import et.gov.ethgov.organization.domain.seal.SealRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateSealUseCase {
    private final SealRepository repository;
    private final Clock clock;

    public UpdateSealUseCase(SealRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SealId execute(UpdateSealCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SealId id = SealId.parse(command.sealId());
        Seal aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("seal not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateSealCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String sealId,
            String reason) {}
}
