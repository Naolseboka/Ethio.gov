package et.gov.ethgov.opendata.application.download;

import et.gov.ethgov.opendata.domain.download.Download;
import et.gov.ethgov.opendata.domain.download.DownloadId;
import et.gov.ethgov.opendata.domain.download.DownloadRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateDownloadUseCase {
    private final DownloadRepository repository;
    private final Clock clock;

    public UpdateDownloadUseCase(DownloadRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DownloadId execute(UpdateDownloadCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DownloadId id = DownloadId.parse(command.downloadId());
        Download aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("download not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateDownloadCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String downloadId,
            String reason) {}
}
