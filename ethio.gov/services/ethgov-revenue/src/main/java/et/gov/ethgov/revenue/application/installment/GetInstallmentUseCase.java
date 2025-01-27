package et.gov.ethgov.revenue.application.installment;

import et.gov.ethgov.revenue.domain.installment.Installment;
import et.gov.ethgov.revenue.domain.installment.InstallmentId;
import et.gov.ethgov.revenue.domain.installment.InstallmentRepository;
import java.util.Objects;

public final class GetInstallmentUseCase {
    private final InstallmentRepository repository;

    public GetInstallmentUseCase(InstallmentRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Installment execute(String rawId) {
        InstallmentId id = InstallmentId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("installment not found: " + rawId));
    }
}
