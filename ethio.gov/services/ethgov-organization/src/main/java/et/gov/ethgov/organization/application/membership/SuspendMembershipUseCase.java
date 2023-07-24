package et.gov.ethgov.organization.application.membership;

import et.gov.ethgov.organization.domain.membership.Membership;
import et.gov.ethgov.organization.domain.membership.MembershipId;
import et.gov.ethgov.organization.domain.membership.MembershipRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendMembershipUseCase {
    private final MembershipRepository repository;
    private final Clock clock;

    public SuspendMembershipUseCase(MembershipRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public MembershipId execute(SuspendMembershipCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        MembershipId id = MembershipId.parse(command.membershipId());
        Membership aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("membership not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendMembershipCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String membershipId,
            String reason) {}
}
