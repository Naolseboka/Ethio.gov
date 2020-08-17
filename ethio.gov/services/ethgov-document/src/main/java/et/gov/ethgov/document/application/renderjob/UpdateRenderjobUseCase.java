package et.gov.ethgov.document.application.renderjob;

import et.gov.ethgov.document.domain.renderjob.Renderjob;
import et.gov.ethgov.document.domain.renderjob.RenderjobId;
import et.gov.ethgov.document.domain.renderjob.RenderjobRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateRenderjobUseCase {
    private final RenderjobRepository repository;
    private final Clock clock;

    public UpdateRenderjobUseCase(RenderjobRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RenderjobId execute(UpdateRenderjobCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RenderjobId id = RenderjobId.parse(command.renderjobId());
        Renderjob aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("renderjob not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateRenderjobCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String renderjobId,
            String reason) {}
}
