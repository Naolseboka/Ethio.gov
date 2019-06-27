package et.gov.ethgov.citizen.application.educationrecord;

import et.gov.ethgov.citizen.domain.educationrecord.Educationrecord;
import et.gov.ethgov.citizen.domain.educationrecord.EducationrecordId;
import et.gov.ethgov.citizen.domain.educationrecord.EducationrecordRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateEducationrecordUseCase {
    private final EducationrecordRepository repository;
    private final Clock clock;

    public CreateEducationrecordUseCase(EducationrecordRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public EducationrecordId execute(CreateEducationrecordCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Educationrecord aggregate = Educationrecord.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateEducationrecordCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String educationrecordId,
            String reason) {}
}
