package et.gov.ethgov.licensing.application.license;

import et.gov.ethgov.licensing.domain.license.License;
import et.gov.ethgov.licensing.domain.license.LicenseId;
import et.gov.ethgov.licensing.domain.license.LicenseRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreateLicenseUseCase {
    private final LicenseRepository repository;
    private final Clock clock;

    public CreateLicenseUseCase(LicenseRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public LicenseId execute(CreateLicenseCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        License aggregate = License.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreateLicenseCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String licenseId,
            String reason) {}
}
