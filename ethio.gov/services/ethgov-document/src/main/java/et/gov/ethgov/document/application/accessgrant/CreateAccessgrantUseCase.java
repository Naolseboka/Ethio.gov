package et.gov.ethgov.document.application.accessgrant;

import et.gov.ethgov.document.domain.accessgrant.Accessgrant;
import et.gov.ethgov.document.domain.accessgrant.AccessgrantId;
import et.gov.ethgov.document.domain.accessgrant.AccessgrantRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateAccessgrantUseCase {
    private final AccessgrantRepository repository;
    private final Clock clock;

    public CreateAccessgrantUseCase(AccessgrantRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AccessgrantId execute(CreateAccessgrantCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Accessgrant aggregate = Accessgrant.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateAccessgrantCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String accessgrantId,
            String reason) {}
}
