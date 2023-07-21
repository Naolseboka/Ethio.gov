package et.gov.ethgov.organization.application.membership;

import et.gov.ethgov.organization.domain.membership.Membership;
import et.gov.ethgov.organization.domain.membership.MembershipId;
import et.gov.ethgov.organization.domain.membership.MembershipRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateMembershipUseCase {
    private final MembershipRepository repository;
    private final Clock clock;

    public CreateMembershipUseCase(MembershipRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public MembershipId execute(CreateMembershipCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Membership aggregate = Membership.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateMembershipCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String membershipId,
            String reason) {}
}
