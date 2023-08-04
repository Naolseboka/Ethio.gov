package et.gov.ethgov.organization.application.orgcontact;

import et.gov.ethgov.organization.domain.orgcontact.Orgcontact;
import et.gov.ethgov.organization.domain.orgcontact.OrgcontactId;
import et.gov.ethgov.organization.domain.orgcontact.OrgcontactRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateOrgcontactUseCase {
    private final OrgcontactRepository repository;
    private final Clock clock;

    public CreateOrgcontactUseCase(OrgcontactRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OrgcontactId execute(CreateOrgcontactCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Orgcontact aggregate = Orgcontact.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateOrgcontactCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String orgcontactId,
            String reason) {}
}
