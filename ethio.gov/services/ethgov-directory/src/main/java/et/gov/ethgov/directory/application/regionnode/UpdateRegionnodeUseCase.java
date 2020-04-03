package et.gov.ethgov.directory.application.regionnode;

import et.gov.ethgov.directory.domain.regionnode.Regionnode;
import et.gov.ethgov.directory.domain.regionnode.RegionnodeId;
import et.gov.ethgov.directory.domain.regionnode.RegionnodeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateRegionnodeUseCase {
    private final RegionnodeRepository repository;
    private final Clock clock;

    public UpdateRegionnodeUseCase(RegionnodeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RegionnodeId execute(UpdateRegionnodeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RegionnodeId id = RegionnodeId.parse(command.regionnodeId());
        Regionnode aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("regionnode not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateRegionnodeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String regionnodeId,
            String reason) {}
}
