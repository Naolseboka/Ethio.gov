package et.gov.ethgov.licensing.application.licensefee;

import et.gov.ethgov.licensing.domain.licensefee.Licensefee;
import et.gov.ethgov.licensing.domain.licensefee.LicensefeeId;
import et.gov.ethgov.licensing.domain.licensefee.LicensefeeRepository;
import java.util.Objects;

public final class GetLicensefeeUseCase {
    private final LicensefeeRepository repository;

    public GetLicensefeeUseCase(LicensefeeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Licensefee execute(String rawId) {
        LicensefeeId id = LicensefeeId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("licensefee not found: " + rawId));
    }
}
