package et.gov.ethgov.developer.application.docsversion;

import et.gov.ethgov.developer.domain.docsversion.Docsversion;
import et.gov.ethgov.developer.domain.docsversion.DocsversionId;
import et.gov.ethgov.developer.domain.docsversion.DocsversionRepository;
import java.util.Objects;

public final class GetDocsversionUseCase {
    private final DocsversionRepository repository;

    public GetDocsversionUseCase(DocsversionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Docsversion execute(String rawId) {
        DocsversionId id = DocsversionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("docsversion not found: " + rawId));
    }
}
