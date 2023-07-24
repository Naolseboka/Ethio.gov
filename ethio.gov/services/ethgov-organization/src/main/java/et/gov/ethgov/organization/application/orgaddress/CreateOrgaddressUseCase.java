package et.gov.ethgov.organization.application.orgaddress;

import et.gov.ethgov.organization.domain.orgaddress.Orgaddress;
import et.gov.ethgov.organization.domain.orgaddress.OrgaddressId;
import et.gov.ethgov.organization.domain.orgaddress.OrgaddressRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateOrgaddressUseCase {
    private final OrgaddressRepository repository;
    private final Clock clock;

    public CreateOrgaddressUseCase(OrgaddressRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OrgaddressId execute(CreateOrgaddressCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Orgaddress aggregate = Orgaddress.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateOrgaddressCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String orgaddressId,
            String reason) {}
}
