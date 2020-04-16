package et.gov.ethgov.directory.application.ward;

import et.gov.ethgov.directory.domain.ward.Ward;
import et.gov.ethgov.directory.domain.ward.WardId;
import et.gov.ethgov.directory.domain.ward.WardRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateWardUseCase {
    private final WardRepository repository;
    private final Clock clock;

    public UpdateWardUseCase(WardRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public WardId execute(UpdateWardCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        WardId id = WardId.parse(command.wardId());
        Ward aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ward not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateWardCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String wardId,
            String reason) {}
}
