package et.gov.ethgov.gis.application.crs;

import et.gov.ethgov.gis.domain.crs.Crs;
import et.gov.ethgov.gis.domain.crs.CrsId;
import et.gov.ethgov.gis.domain.crs.CrsRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateCrsUseCase {
    private final CrsRepository repository;
    private final Clock clock;

    public CreateCrsUseCase(CrsRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CrsId execute(CreateCrsCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Crs aggregate = Crs.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateCrsCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String crsId,
            String reason) {}
}
