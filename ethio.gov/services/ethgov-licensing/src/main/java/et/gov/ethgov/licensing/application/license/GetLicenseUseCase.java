package et.gov.ethgov.licensing.application.license;

import et.gov.ethgov.licensing.domain.license.License;
import et.gov.ethgov.licensing.domain.license.LicenseId;
import et.gov.ethgov.licensing.domain.license.LicenseRepository;
import java.util.Objects;

public final class GetLicenseUseCase {
    private final LicenseRepository repository;

    public GetLicenseUseCase(LicenseRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public License execute(String rawId) {
        LicenseId id = LicenseId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("license not found: " + rawId));
    }
}
