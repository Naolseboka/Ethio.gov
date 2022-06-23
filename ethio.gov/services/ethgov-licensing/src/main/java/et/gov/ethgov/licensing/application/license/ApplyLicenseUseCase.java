package et.gov.ethgov.licensing.application.license;

import et.gov.ethgov.licensing.application.LicensingEventPublisher;
import et.gov.ethgov.licensing.domain.license.BusinessLicense;
import et.gov.ethgov.licensing.domain.license.LicenseHolder;
import et.gov.ethgov.licensing.domain.license.LicenseId;
import et.gov.ethgov.licensing.domain.license.LicenseNumber;
import et.gov.ethgov.licensing.domain.license.LicenseNumberSequence;
import et.gov.ethgov.licensing.domain.license.LicenseRepository;
import et.gov.ethgov.licensing.domain.license.LicenseType;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplyLicenseUseCase {

    private final LicenseRepository licenses;
    private final LicenseNumberSequence sequences;
    private final LicensingEventPublisher eventPublisher;
    private final Clock clock;

    public ApplyLicenseUseCase(
            LicenseRepository licenses,
            LicenseNumberSequence sequences,
            LicensingEventPublisher eventPublisher,
            Clock clock) {
        this.licenses = licenses;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public LicenseId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        LicenseNumber licenseNumber = LicenseNumber.allocate(year, sequences.nextSequence(tenantId, year));
        BusinessLicense license = BusinessLicense.apply(
                tenantId,
                licenseNumber,
                LicenseType.valueOf(command.type()),
                command.businessName(),
                command.activityDescription(),
                LicenseHolder.of(command.organizationId(), command.citizenId()),
                command.region(),
                command.zone(),
                command.woreda(),
                command.kebele(),
                command.locality(),
                command.parcelId(),
                command.linkedCaseId(),
                clock.instant());
        licenses.save(license);
        eventPublisher.publishAll(license.pullDomainEvents());
        return license.id();
    }

    public record Command(
            String tenantId,
            String type,
            String businessName,
            String activityDescription,
            String organizationId,
            String citizenId,
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            String parcelId,
            String linkedCaseId) {}
}
