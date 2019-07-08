package et.gov.ethgov.citizen.application.namechange;

import et.gov.ethgov.citizen.domain.namechange.Namechange;
import et.gov.ethgov.citizen.domain.namechange.NamechangeId;
import et.gov.ethgov.citizen.domain.namechange.NamechangeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateNamechangeUseCase {
    private final NamechangeRepository repository;
    private final Clock clock;

    public CreateNamechangeUseCase(NamechangeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public NamechangeId execute(CreateNamechangeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Namechange aggregate = Namechange.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateNamechangeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String namechangeId,
            String reason) {}
}
