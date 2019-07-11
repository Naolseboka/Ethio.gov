package et.gov.ethgov.citizen.application.namechange;

import et.gov.ethgov.citizen.domain.namechange.Namechange;
import et.gov.ethgov.citizen.domain.namechange.NamechangeId;
import et.gov.ethgov.citizen.domain.namechange.NamechangeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendNamechangeUseCase {
    private final NamechangeRepository repository;
    private final Clock clock;

    public SuspendNamechangeUseCase(NamechangeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public NamechangeId execute(SuspendNamechangeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        NamechangeId id = NamechangeId.parse(command.namechangeId());
        Namechange aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("namechange not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendNamechangeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String namechangeId,
            String reason) {}
}
