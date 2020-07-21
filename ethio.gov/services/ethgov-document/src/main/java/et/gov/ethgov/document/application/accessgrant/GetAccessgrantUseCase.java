package et.gov.ethgov.document.application.accessgrant;

import et.gov.ethgov.document.domain.accessgrant.Accessgrant;
import et.gov.ethgov.document.domain.accessgrant.AccessgrantId;
import et.gov.ethgov.document.domain.accessgrant.AccessgrantRepository;
import java.util.Objects;

public final class GetAccessgrantUseCase {
    private final AccessgrantRepository repository;

    public GetAccessgrantUseCase(AccessgrantRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Accessgrant execute(String rawId) {
        AccessgrantId id = AccessgrantId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("accessgrant not found: " + rawId));
    }
}
