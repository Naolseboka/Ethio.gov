package et.gov.ethgov.organization.application.orgaddress;

import et.gov.ethgov.organization.domain.orgaddress.Orgaddress;
import et.gov.ethgov.organization.domain.orgaddress.OrgaddressId;
import et.gov.ethgov.organization.domain.orgaddress.OrgaddressRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendOrgaddressUseCase {
    private final OrgaddressRepository repository;
    private final Clock clock;

    public SuspendOrgaddressUseCase(OrgaddressRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OrgaddressId execute(SuspendOrgaddressCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        OrgaddressId id = OrgaddressId.parse(command.orgaddressId());
        Orgaddress aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("orgaddress not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendOrgaddressCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String orgaddressId,
            String reason) {}
}
