package et.gov.ethgov.document.application.accessgrant;

import et.gov.ethgov.document.domain.accessgrant.Accessgrant;
import et.gov.ethgov.document.domain.accessgrant.AccessgrantId;
import et.gov.ethgov.document.domain.accessgrant.AccessgrantRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateAccessgrantUseCase {
    private final AccessgrantRepository repository;
    private final Clock clock;

    public UpdateAccessgrantUseCase(AccessgrantRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AccessgrantId execute(UpdateAccessgrantCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AccessgrantId id = AccessgrantId.parse(command.accessgrantId());
        Accessgrant aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("accessgrant not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateAccessgrantCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String accessgrantId,
            String reason) {}
}
