package et.gov.ethgov.document.application.ocrjob;

import et.gov.ethgov.document.domain.ocrjob.Ocrjob;
import et.gov.ethgov.document.domain.ocrjob.OcrjobId;
import et.gov.ethgov.document.domain.ocrjob.OcrjobRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendOcrjobUseCase {
    private final OcrjobRepository repository;
    private final Clock clock;

    public SuspendOcrjobUseCase(OcrjobRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OcrjobId execute(SuspendOcrjobCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        OcrjobId id = OcrjobId.parse(command.ocrjobId());
        Ocrjob aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ocrjob not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendOcrjobCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String ocrjobId,
            String reason) {}
}
