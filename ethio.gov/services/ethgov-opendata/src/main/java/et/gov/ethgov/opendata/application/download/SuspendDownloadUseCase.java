package et.gov.ethgov.opendata.application.download;

import et.gov.ethgov.opendata.domain.download.Download;
import et.gov.ethgov.opendata.domain.download.DownloadId;
import et.gov.ethgov.opendata.domain.download.DownloadRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendDownloadUseCase {
    private final DownloadRepository repository;
    private final Clock clock;

    public SuspendDownloadUseCase(DownloadRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DownloadId execute(SuspendDownloadCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        DownloadId id = DownloadId.parse(command.downloadId());
        Download aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("download not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendDownloadCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String downloadId,
            String reason) {}
}
