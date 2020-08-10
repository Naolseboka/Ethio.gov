package et.gov.ethgov.document.application.redaction;

import et.gov.ethgov.document.domain.redaction.Redaction;
import et.gov.ethgov.document.domain.redaction.RedactionId;
import et.gov.ethgov.document.domain.redaction.RedactionRepository;
import java.util.Objects;

public final class GetRedactionUseCase {
    private final RedactionRepository repository;

    public GetRedactionUseCase(RedactionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Redaction execute(String rawId) {
        RedactionId id = RedactionId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("redaction not found: " + rawId));
    }
}
