package et.gov.ethgov.document.application.renderjob;

import et.gov.ethgov.document.domain.renderjob.Renderjob;
import et.gov.ethgov.document.domain.renderjob.RenderjobId;
import et.gov.ethgov.document.domain.renderjob.RenderjobRepository;
import java.util.Objects;

public final class GetRenderjobUseCase {
    private final RenderjobRepository repository;

    public GetRenderjobUseCase(RenderjobRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Renderjob execute(String rawId) {
        RenderjobId id = RenderjobId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("renderjob not found: " + rawId));
    }
}
