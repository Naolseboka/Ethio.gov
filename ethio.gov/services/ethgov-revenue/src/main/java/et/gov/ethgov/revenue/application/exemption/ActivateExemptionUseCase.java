package et.gov.ethgov.revenue.application.exemption;

import et.gov.ethgov.revenue.domain.exemption.Exemption;
import et.gov.ethgov.revenue.domain.exemption.ExemptionId;
import et.gov.ethgov.revenue.domain.exemption.ExemptionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateExemptionUseCase {
    private final ExemptionRepository repository;
    private final Clock clock;

    public ActivateExemptionUseCase(ExemptionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ExemptionId execute(ActivateExemptionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ExemptionId id = ExemptionId.parse(command.exemptionId());
        Exemption aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("exemption not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateExemptionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String exemptionId,
            String reason) {}
}
