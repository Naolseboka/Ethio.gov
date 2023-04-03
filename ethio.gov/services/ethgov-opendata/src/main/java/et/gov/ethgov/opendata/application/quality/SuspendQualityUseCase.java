package et.gov.ethgov.opendata.application.quality;

import et.gov.ethgov.opendata.domain.quality.Quality;
import et.gov.ethgov.opendata.domain.quality.QualityId;
import et.gov.ethgov.opendata.domain.quality.QualityRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendQualityUseCase {
    private final QualityRepository repository;
    private final Clock clock;

    public SuspendQualityUseCase(QualityRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public QualityId execute(SuspendQualityCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        QualityId id = QualityId.parse(command.qualityId());
        Quality aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("quality not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendQualityCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String qualityId,
            String reason) {}
}
