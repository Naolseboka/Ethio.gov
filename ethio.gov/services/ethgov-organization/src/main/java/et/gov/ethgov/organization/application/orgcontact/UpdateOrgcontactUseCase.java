package et.gov.ethgov.organization.application.orgcontact;

import et.gov.ethgov.organization.domain.orgcontact.Orgcontact;
import et.gov.ethgov.organization.domain.orgcontact.OrgcontactId;
import et.gov.ethgov.organization.domain.orgcontact.OrgcontactRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateOrgcontactUseCase {
    private final OrgcontactRepository repository;
    private final Clock clock;

    public UpdateOrgcontactUseCase(OrgcontactRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OrgcontactId execute(UpdateOrgcontactCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        OrgcontactId id = OrgcontactId.parse(command.orgcontactId());
        Orgcontact aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("orgcontact not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateOrgcontactCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String orgcontactId,
            String reason) {}
}
