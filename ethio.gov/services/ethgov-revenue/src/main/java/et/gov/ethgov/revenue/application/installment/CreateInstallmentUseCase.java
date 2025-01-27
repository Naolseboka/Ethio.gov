package et.gov.ethgov.revenue.application.installment;

import et.gov.ethgov.revenue.domain.installment.Installment;
import et.gov.ethgov.revenue.domain.installment.InstallmentId;
import et.gov.ethgov.revenue.domain.installment.InstallmentRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateInstallmentUseCase {
    private final InstallmentRepository repository;
    private final Clock clock;

    public CreateInstallmentUseCase(InstallmentRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public InstallmentId execute(CreateInstallmentCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Installment aggregate = Installment.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateInstallmentCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String installmentId,
            String reason) {}
}
