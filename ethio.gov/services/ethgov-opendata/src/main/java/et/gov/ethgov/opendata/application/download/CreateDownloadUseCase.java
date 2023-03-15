package et.gov.ethgov.opendata.application.download;

import et.gov.ethgov.opendata.domain.download.Download;
import et.gov.ethgov.opendata.domain.download.DownloadId;
import et.gov.ethgov.opendata.domain.download.DownloadRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateDownloadUseCase {
    private final DownloadRepository repository;
    private final Clock clock;

    public CreateDownloadUseCase(DownloadRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public DownloadId execute(CreateDownloadCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Download aggregate = Download.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateDownloadCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String downloadId,
            String reason) {}
}
