package et.gov.ethgov.document.application.accessgrant;

import et.gov.ethgov.document.domain.accessgrant.Accessgrant;
import et.gov.ethgov.document.domain.accessgrant.AccessgrantId;
import et.gov.ethgov.document.domain.accessgrant.AccessgrantRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendAccessgrantUseCase {
    private final AccessgrantRepository repository;
    private final Clock clock;

    public SuspendAccessgrantUseCase(AccessgrantRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AccessgrantId execute(SuspendAccessgrantCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AccessgrantId id = AccessgrantId.parse(command.accessgrantId());
        Accessgrant aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("accessgrant not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendAccessgrantCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String accessgrantId,
            String reason) {}
}
