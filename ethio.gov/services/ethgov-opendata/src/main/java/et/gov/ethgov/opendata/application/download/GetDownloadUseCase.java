package et.gov.ethgov.opendata.application.download;

import et.gov.ethgov.opendata.domain.download.Download;
import et.gov.ethgov.opendata.domain.download.DownloadId;
import et.gov.ethgov.opendata.domain.download.DownloadRepository;
import java.util.Objects;

public final class GetDownloadUseCase {
    private final DownloadRepository repository;

    public GetDownloadUseCase(DownloadRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Download execute(String rawId) {
        DownloadId id = DownloadId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("download not found: " + rawId));
    }
}
