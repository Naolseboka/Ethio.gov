package et.gov.ethgov.directory.application.directoryentry;

import et.gov.ethgov.directory.domain.directoryentry.Directoryentry;
import et.gov.ethgov.directory.domain.directoryentry.DirectoryentryId;
import et.gov.ethgov.directory.domain.directoryentry.DirectoryentryRepository;
import java.util.Objects;

public final class GetDirectoryentryUseCase {
    private final DirectoryentryRepository repository;

    public GetDirectoryentryUseCase(DirectoryentryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Directoryentry execute(String rawId) {
        DirectoryentryId id = DirectoryentryId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("directoryentry not found: " + rawId));
    }
}
