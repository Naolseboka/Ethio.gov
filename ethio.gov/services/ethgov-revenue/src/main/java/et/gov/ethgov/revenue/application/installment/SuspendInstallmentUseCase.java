package et.gov.ethgov.revenue.application.installment;

import et.gov.ethgov.revenue.domain.installment.Installment;
import et.gov.ethgov.revenue.domain.installment.InstallmentId;
import et.gov.ethgov.revenue.domain.installment.InstallmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendInstallmentUseCase {
    private final InstallmentRepository repository;
    private final Clock clock;

    public SuspendInstallmentUseCase(InstallmentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public InstallmentId execute(SuspendInstallmentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        InstallmentId id = InstallmentId.parse(command.installmentId());
        Installment aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("installment not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendInstallmentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String installmentId,
            String reason) {}
}
