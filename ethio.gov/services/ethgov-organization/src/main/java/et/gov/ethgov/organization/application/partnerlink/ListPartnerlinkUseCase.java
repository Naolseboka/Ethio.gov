package et.gov.ethgov.organization.application.partnerlink;

import et.gov.ethgov.organization.domain.partnerlink.Partnerlink;
import et.gov.ethgov.organization.domain.partnerlink.PartnerlinkRepository;
import java.util.List;
import java.util.Objects;

public final class ListPartnerlinkUseCase {
    private final PartnerlinkRepository repository;

    public ListPartnerlinkUseCase(PartnerlinkRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Partnerlink> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
