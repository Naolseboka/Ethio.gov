package et.gov.ethgov.organization.application.membership;

import et.gov.ethgov.organization.domain.membership.Membership;
import et.gov.ethgov.organization.domain.membership.MembershipRepository;
import java.util.List;
import java.util.Objects;

public final class ListMembershipUseCase {
    private final MembershipRepository repository;

    public ListMembershipUseCase(MembershipRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Membership> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
