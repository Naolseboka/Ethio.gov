package et.gov.ethgov.organization.application.orgunit;

import et.gov.ethgov.organization.domain.orgunit.Orgunit;
import et.gov.ethgov.organization.domain.orgunit.OrgunitId;
import et.gov.ethgov.organization.domain.orgunit.OrgunitRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateOrgunitUseCase {
    private final OrgunitRepository repository;
    private final Clock clock;

    public CreateOrgunitUseCase(OrgunitRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public OrgunitId execute(CreateOrgunitCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Orgunit aggregate = Orgunit.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateOrgunitCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String orgunitId,
            String reason) {}
}
