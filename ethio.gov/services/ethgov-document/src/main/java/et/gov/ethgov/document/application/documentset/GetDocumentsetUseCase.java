package et.gov.ethgov.document.application.documentset;

import et.gov.ethgov.document.domain.documentset.Documentset;
import et.gov.ethgov.document.domain.documentset.DocumentsetId;
import et.gov.ethgov.document.domain.documentset.DocumentsetRepository;
import java.util.Objects;

public final class GetDocumentsetUseCase {
    private final DocumentsetRepository repository;

    public GetDocumentsetUseCase(DocumentsetRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Documentset execute(String rawId) {
        DocumentsetId id = DocumentsetId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("documentset not found: " + rawId));
    }
}
