package et.gov.ethgov.directory.application.ward;

import et.gov.ethgov.directory.domain.ward.Ward;
import et.gov.ethgov.directory.domain.ward.WardId;
import et.gov.ethgov.directory.domain.ward.WardRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendWardUseCase {
    private final WardRepository repository;
    private final Clock clock;

    public SuspendWardUseCase(WardRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public WardId execute(SuspendWardCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        WardId id = WardId.parse(command.wardId());
        Ward aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ward not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendWardCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String wardId,
            String reason) {}
}
