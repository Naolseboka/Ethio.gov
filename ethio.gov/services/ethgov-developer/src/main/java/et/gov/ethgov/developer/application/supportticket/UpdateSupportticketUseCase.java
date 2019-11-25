package et.gov.ethgov.developer.application.supportticket;

import et.gov.ethgov.developer.domain.supportticket.Supportticket;
import et.gov.ethgov.developer.domain.supportticket.SupportticketId;
import et.gov.ethgov.developer.domain.supportticket.SupportticketRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateSupportticketUseCase {
    private final SupportticketRepository repository;
    private final Clock clock;

    public UpdateSupportticketUseCase(SupportticketRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public SupportticketId execute(UpdateSupportticketCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        SupportticketId id = SupportticketId.parse(command.supportticketId());
        Supportticket aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("supportticket not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateSupportticketCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String supportticketId,
            String reason) {}
}
