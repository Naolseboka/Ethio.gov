package et.gov.ethgov.revenue.application.exemption;

import et.gov.ethgov.revenue.domain.exemption.Exemption;
import et.gov.ethgov.revenue.domain.exemption.ExemptionId;
import et.gov.ethgov.revenue.domain.exemption.ExemptionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateExemptionUseCase {
    private final ExemptionRepository repository;
    private final Clock clock;

    public UpdateExemptionUseCase(ExemptionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ExemptionId execute(UpdateExemptionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        ExemptionId id = ExemptionId.parse(command.exemptionId());
        Exemption aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("exemption not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateExemptionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String exemptionId,
            String reason) {}
}
