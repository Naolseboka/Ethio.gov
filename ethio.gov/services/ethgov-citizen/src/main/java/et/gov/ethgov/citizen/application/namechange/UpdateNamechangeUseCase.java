package et.gov.ethgov.citizen.application.namechange;

import et.gov.ethgov.citizen.domain.namechange.Namechange;
import et.gov.ethgov.citizen.domain.namechange.NamechangeId;
import et.gov.ethgov.citizen.domain.namechange.NamechangeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateNamechangeUseCase {
    private final NamechangeRepository repository;
    private final Clock clock;

    public UpdateNamechangeUseCase(NamechangeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public NamechangeId execute(UpdateNamechangeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        NamechangeId id = NamechangeId.parse(command.namechangeId());
        Namechange aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("namechange not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateNamechangeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String namechangeId,
            String reason) {}
}
