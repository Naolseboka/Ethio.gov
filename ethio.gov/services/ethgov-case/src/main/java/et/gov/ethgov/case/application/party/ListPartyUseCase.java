package et.gov.ethgov.case.application.party;

import et.gov.ethgov.case.domain.party.Party;
import et.gov.ethgov.case.domain.party.PartyRepository;
import java.util.List;
import java.util.Objects;

public final class ListPartyUseCase {
    private final PartyRepository repository;

    public ListPartyUseCase(PartyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Party> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
