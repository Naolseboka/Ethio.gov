package et.gov.ethgov.document.application.ocrjob;

import et.gov.ethgov.document.domain.ocrjob.Ocrjob;
import et.gov.ethgov.document.domain.ocrjob.OcrjobId;
import et.gov.ethgov.document.domain.ocrjob.OcrjobRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateOcrjobUseCase {
    private final OcrjobRepository repository;
    private final Clock clock;

    public CreateOcrjobUseCase(OcrjobRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OcrjobId execute(CreateOcrjobCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Ocrjob aggregate = Ocrjob.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateOcrjobCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String ocrjobId,
            String reason) {}
}
