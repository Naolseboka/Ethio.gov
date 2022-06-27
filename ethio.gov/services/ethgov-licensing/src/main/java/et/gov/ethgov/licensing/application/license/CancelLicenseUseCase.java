package et.gov.ethgov.licensing.application.license;

import et.gov.ethgov.licensing.application.LicensingEventPublisher;
import et.gov.ethgov.licensing.domain.license.BusinessLicense;
import et.gov.ethgov.licensing.domain.license.LicenseId;
import et.gov.ethgov.licensing.domain.license.LicenseRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelLicenseUseCase {

    private final LicenseRepository licenses;
    private final LicensingEventPublisher eventPublisher;
    private final Clock clock;

    public CancelLicenseUseCase(LicenseRepository licenses, LicensingEventPublisher eventPublisher, Clock clock) {
        this.licenses = licenses;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String licenseId, String reason) {
        Objects.requireNonNull(licenseId, "licenseId");
        BusinessLicense license = licenses.findById(LicenseId.of(licenseId))
                .orElseThrow(() -> new DomainException(
                        "LICENSE_NOT_FOUND", "License Not Found", "License does not exist", 404));
        license.cancel(reason, clock.instant());
        licenses.save(license);
        eventPublisher.publishAll(license.pullDomainEvents());
    }
}
