package et.gov.ethgov.notification.application.digest;

import et.gov.ethgov.notification.domain.digest.Digest;
import et.gov.ethgov.notification.domain.digest.DigestRepository;
import java.util.List;
import java.util.Objects;

public final class ListDigestUseCase {
    private final DigestRepository repository;

    public ListDigestUseCase(DigestRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Digest> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
