package et.gov.ethgov.directory.application.regionnode;

import et.gov.ethgov.directory.domain.regionnode.Regionnode;
import et.gov.ethgov.directory.domain.regionnode.RegionnodeId;
import et.gov.ethgov.directory.domain.regionnode.RegionnodeRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateRegionnodeUseCase {
    private final RegionnodeRepository repository;
    private final Clock clock;

    public CreateRegionnodeUseCase(RegionnodeRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RegionnodeId execute(CreateRegionnodeCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Regionnode aggregate = Regionnode.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateRegionnodeCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String regionnodeId,
            String reason) {}
}
