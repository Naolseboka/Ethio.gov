package et.gov.ethgov.directory.application.directoryentry;

import et.gov.ethgov.directory.domain.directoryentry.Directoryentry;
import et.gov.ethgov.directory.domain.directoryentry.DirectoryentryRepository;
import java.util.List;
import java.util.Objects;

public final class ListDirectoryentryUseCase {
    private final DirectoryentryRepository repository;

    public ListDirectoryentryUseCase(DirectoryentryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Directoryentry> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
