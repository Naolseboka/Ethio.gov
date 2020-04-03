package et.gov.ethgov.directory.application.regionnode;

import et.gov.ethgov.directory.domain.regionnode.Regionnode;
import et.gov.ethgov.directory.domain.regionnode.RegionnodeId;
import et.gov.ethgov.directory.domain.regionnode.RegionnodeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendRegionnodeUseCase {
    private final RegionnodeRepository repository;
    private final Clock clock;

    public SuspendRegionnodeUseCase(RegionnodeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RegionnodeId execute(SuspendRegionnodeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RegionnodeId id = RegionnodeId.parse(command.regionnodeId());
        Regionnode aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("regionnode not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendRegionnodeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String regionnodeId,
            String reason) {}
}
