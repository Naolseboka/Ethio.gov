package et.gov.ethgov.citizen.application.educationrecord;

import et.gov.ethgov.citizen.domain.educationrecord.Educationrecord;
import et.gov.ethgov.citizen.domain.educationrecord.EducationrecordId;
import et.gov.ethgov.citizen.domain.educationrecord.EducationrecordRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateEducationrecordUseCase {
    private final EducationrecordRepository repository;
    private final Clock clock;

    public ActivateEducationrecordUseCase(EducationrecordRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EducationrecordId execute(ActivateEducationrecordCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        EducationrecordId id = EducationrecordId.parse(command.educationrecordId());
        Educationrecord aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("educationrecord not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateEducationrecordCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String educationrecordId,
            String reason) {}
}
