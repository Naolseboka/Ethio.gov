package et.gov.ethgov.citizen.application.documentlink;

import et.gov.ethgov.citizen.domain.documentlink.Documentlink;
import et.gov.ethgov.citizen.domain.documentlink.DocumentlinkId;
import et.gov.ethgov.citizen.domain.documentlink.DocumentlinkRepository;
import java.util.Objects;

public final class GetDocumentlinkUseCase {
    private final DocumentlinkRepository repository;

    public GetDocumentlinkUseCase(DocumentlinkRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Documentlink execute(String rawId) {
        DocumentlinkId id = DocumentlinkId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("documentlink not found: " + rawId));
    }
}
