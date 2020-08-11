package et.gov.ethgov.document.application.renderjob;

import et.gov.ethgov.document.domain.renderjob.Renderjob;
import et.gov.ethgov.document.domain.renderjob.RenderjobId;
import et.gov.ethgov.document.domain.renderjob.RenderjobRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateRenderjobUseCase {
    private final RenderjobRepository repository;
    private final Clock clock;

    public CreateRenderjobUseCase(RenderjobRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RenderjobId execute(CreateRenderjobCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Renderjob aggregate = Renderjob.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateRenderjobCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String renderjobId,
            String reason) {}
}
