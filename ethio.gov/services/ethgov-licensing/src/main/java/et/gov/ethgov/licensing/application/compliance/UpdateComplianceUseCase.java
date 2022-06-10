package et.gov.ethgov.licensing.application.compliance;

import et.gov.ethgov.licensing.domain.compliance.Compliance;
import et.gov.ethgov.licensing.domain.compliance.ComplianceId;
import et.gov.ethgov.licensing.domain.compliance.ComplianceRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateComplianceUseCase {
    private final ComplianceRepository repository;
    private final Clock clock;

    public UpdateComplianceUseCase(ComplianceRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ComplianceId execute(UpdateComplianceCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ComplianceId id = ComplianceId.parse(command.complianceId());
        Compliance aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("compliance not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateComplianceCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String complianceId,
            String reason) {}
}
