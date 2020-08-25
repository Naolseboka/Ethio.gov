package et.gov.ethgov.document.application.signature;

import et.gov.ethgov.document.domain.signature.Signature;
import et.gov.ethgov.document.domain.signature.SignatureRepository;
import java.util.List;
import java.util.Objects;

public final class ListSignatureUseCase {
    private final SignatureRepository repository;

    public ListSignatureUseCase(SignatureRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Signature> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
