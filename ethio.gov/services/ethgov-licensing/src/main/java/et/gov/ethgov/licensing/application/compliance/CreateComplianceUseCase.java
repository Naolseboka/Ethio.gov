package et.gov.ethgov.licensing.application.compliance;

import et.gov.ethgov.licensing.domain.compliance.Compliance;
import et.gov.ethgov.licensing.domain.compliance.ComplianceId;
import et.gov.ethgov.licensing.domain.compliance.ComplianceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateComplianceUseCase {
    private final ComplianceRepository repository;
    private final Clock clock;

    public CreateComplianceUseCase(ComplianceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ComplianceId execute(CreateComplianceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Compliance aggregate = Compliance.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateComplianceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String complianceId,
            String reason) {}
}
