package et.gov.ethgov.gis.application.crs;

import et.gov.ethgov.gis.domain.crs.Crs;
import et.gov.ethgov.gis.domain.crs.CrsId;
import et.gov.ethgov.gis.domain.crs.CrsRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateCrsUseCase {
    private final CrsRepository repository;
    private final Clock clock;

    public UpdateCrsUseCase(CrsRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CrsId execute(UpdateCrsCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CrsId id = CrsId.parse(command.crsId());
        Crs aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("crs not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateCrsCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String crsId,
            String reason) {}
}
