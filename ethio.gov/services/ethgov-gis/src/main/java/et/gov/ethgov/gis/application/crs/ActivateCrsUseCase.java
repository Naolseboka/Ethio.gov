package et.gov.ethgov.gis.application.crs;

import et.gov.ethgov.gis.domain.crs.Crs;
import et.gov.ethgov.gis.domain.crs.CrsId;
import et.gov.ethgov.gis.domain.crs.CrsRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateCrsUseCase {
    private final CrsRepository repository;
    private final Clock clock;

    public ActivateCrsUseCase(CrsRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public CrsId execute(ActivateCrsCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        CrsId id = CrsId.parse(command.crsId());
        Crs aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("crs not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateCrsCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String crsId,
            String reason) {}
}
