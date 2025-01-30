package et.gov.ethgov.revenue.application.installment;

import et.gov.ethgov.revenue.domain.installment.Installment;
import et.gov.ethgov.revenue.domain.installment.InstallmentId;
import et.gov.ethgov.revenue.domain.installment.InstallmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateInstallmentUseCase {
    private final InstallmentRepository repository;
    private final Clock clock;

    public UpdateInstallmentUseCase(InstallmentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public InstallmentId execute(UpdateInstallmentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        InstallmentId id = InstallmentId.parse(command.installmentId());
        Installment aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("installment not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateInstallmentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String installmentId,
            String reason) {}
}
