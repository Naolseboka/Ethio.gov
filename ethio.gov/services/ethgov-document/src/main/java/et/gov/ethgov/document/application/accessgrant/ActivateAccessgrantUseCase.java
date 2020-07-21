package et.gov.ethgov.document.application.accessgrant;

import et.gov.ethgov.document.domain.accessgrant.Accessgrant;
import et.gov.ethgov.document.domain.accessgrant.AccessgrantId;
import et.gov.ethgov.document.domain.accessgrant.AccessgrantRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateAccessgrantUseCase {
    private final AccessgrantRepository repository;
    private final Clock clock;

    public ActivateAccessgrantUseCase(AccessgrantRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public AccessgrantId execute(ActivateAccessgrantCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        AccessgrantId id = AccessgrantId.parse(command.accessgrantId());
        Accessgrant aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("accessgrant not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateAccessgrantCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String accessgrantId,
            String reason) {}
}
