package et.gov.ethgov.licensing.application.license;

import et.gov.ethgov.licensing.domain.license.License;
import et.gov.ethgov.licensing.domain.license.LicenseId;
import et.gov.ethgov.licensing.domain.license.LicenseRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdateLicenseUseCase {
    private final LicenseRepository repository;
    private final Clock clock;

    public UpdateLicenseUseCase(LicenseRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public LicenseId execute(UpdateLicenseCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        LicenseId id = LicenseId.parse(command.licenseId());
        License aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("license not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdateLicenseCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String licenseId,
            String reason) {}
}
