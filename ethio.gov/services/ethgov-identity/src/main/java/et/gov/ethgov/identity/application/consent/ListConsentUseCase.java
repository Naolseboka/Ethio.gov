package et.gov.ethgov.identity.application.consent;

import et.gov.ethgov.identity.domain.consent.Consent;
import et.gov.ethgov.identity.domain.consent.ConsentRepository;
import java.util.List;
import java.util.Objects;

public final class ListConsentUseCase {
    private final ConsentRepository repository;

    public ListConsentUseCase(ConsentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Consent> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
