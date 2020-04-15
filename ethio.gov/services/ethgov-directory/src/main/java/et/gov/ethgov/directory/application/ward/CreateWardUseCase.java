package et.gov.ethgov.directory.application.ward;

import et.gov.ethgov.directory.domain.ward.Ward;
import et.gov.ethgov.directory.domain.ward.WardId;
import et.gov.ethgov.directory.domain.ward.WardRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateWardUseCase {
    private final WardRepository repository;
    private final Clock clock;

    public CreateWardUseCase(WardRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public WardId execute(CreateWardCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Ward aggregate = Ward.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateWardCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String wardId,
            String reason) {}
}
