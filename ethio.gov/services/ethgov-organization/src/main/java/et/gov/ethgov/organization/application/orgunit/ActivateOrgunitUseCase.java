package et.gov.ethgov.organization.application.orgunit;

import et.gov.ethgov.organization.domain.orgunit.Orgunit;
import et.gov.ethgov.organization.domain.orgunit.OrgunitId;
import et.gov.ethgov.organization.domain.orgunit.OrgunitRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateOrgunitUseCase {
    private final OrgunitRepository repository;
    private final Clock clock;

    public ActivateOrgunitUseCase(OrgunitRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OrgunitId execute(ActivateOrgunitCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        OrgunitId id = OrgunitId.parse(command.orgunitId());
        Orgunit aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("orgunit not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateOrgunitCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String orgunitId,
            String reason) {}
}
