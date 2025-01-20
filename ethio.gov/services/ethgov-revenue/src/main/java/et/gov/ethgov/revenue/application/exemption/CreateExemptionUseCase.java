package et.gov.ethgov.revenue.application.exemption;

import et.gov.ethgov.revenue.domain.exemption.Exemption;
import et.gov.ethgov.revenue.domain.exemption.ExemptionId;
import et.gov.ethgov.revenue.domain.exemption.ExemptionRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateExemptionUseCase {
    private final ExemptionRepository repository;
    private final Clock clock;

    public CreateExemptionUseCase(ExemptionRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public ExemptionId execute(CreateExemptionCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Exemption aggregate = Exemption.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateExemptionCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String exemptionId,
            String reason) {}
}
