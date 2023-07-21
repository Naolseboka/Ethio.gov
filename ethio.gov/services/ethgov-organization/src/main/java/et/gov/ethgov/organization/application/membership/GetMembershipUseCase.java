package et.gov.ethgov.organization.application.membership;

import et.gov.ethgov.organization.domain.membership.Membership;
import et.gov.ethgov.organization.domain.membership.MembershipId;
import et.gov.ethgov.organization.domain.membership.MembershipRepository;
import java.util.Objects;

public final class GetMembershipUseCase {
    private final MembershipRepository repository;

    public GetMembershipUseCase(MembershipRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Membership execute(String rawId) {
        MembershipId id = MembershipId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("membership not found: " + rawId));
    }
}
