package et.gov.ethgov.opendata.application.quality;

import et.gov.ethgov.opendata.domain.quality.Quality;
import et.gov.ethgov.opendata.domain.quality.QualityId;
import et.gov.ethgov.opendata.domain.quality.QualityRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateQualityUseCase {
    private final QualityRepository repository;
    private final Clock clock;

    public CreateQualityUseCase(QualityRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public QualityId execute(CreateQualityCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Quality aggregate = Quality.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateQualityCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String qualityId,
            String reason) {}
}
