package et.gov.ethgov.document.application.archivebox;

import et.gov.ethgov.document.domain.archivebox.Archivebox;
import et.gov.ethgov.document.domain.archivebox.ArchiveboxId;
import et.gov.ethgov.document.domain.archivebox.ArchiveboxRepository;
import java.util.Objects;

public final class GetArchiveboxUseCase {
    private final ArchiveboxRepository repository;

    public GetArchiveboxUseCase(ArchiveboxRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Archivebox execute(String rawId) {
        ArchiveboxId id = ArchiveboxId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("archivebox not found: " + rawId));
    }
}
