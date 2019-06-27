package et.gov.ethgov.citizen.application.educationrecord;

import et.gov.ethgov.citizen.domain.educationrecord.Educationrecord;
import et.gov.ethgov.citizen.domain.educationrecord.EducationrecordId;
import et.gov.ethgov.citizen.domain.educationrecord.EducationrecordRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendEducationrecordUseCase {
    private final EducationrecordRepository repository;
    private final Clock clock;

    public SuspendEducationrecordUseCase(EducationrecordRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EducationrecordId execute(SuspendEducationrecordCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        EducationrecordId id = EducationrecordId.parse(command.educationrecordId());
        Educationrecord aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("educationrecord not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendEducationrecordCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String educationrecordId,
            String reason) {}
}
