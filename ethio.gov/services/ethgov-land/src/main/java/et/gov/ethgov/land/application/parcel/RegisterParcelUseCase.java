package et.gov.ethgov.land.application.parcel;

import et.gov.ethgov.land.application.LandEventPublisher;
import et.gov.ethgov.land.domain.parcel.AreaSquareMeters;
import et.gov.ethgov.land.domain.parcel.LandParcel;
import et.gov.ethgov.land.domain.parcel.LandUse;
import et.gov.ethgov.land.domain.parcel.ParcelId;
import et.gov.ethgov.land.domain.parcel.ParcelNumber;
import et.gov.ethgov.land.domain.parcel.ParcelNumberSequence;
import et.gov.ethgov.land.domain.parcel.ParcelOwner;
import et.gov.ethgov.land.domain.parcel.ParcelRepository;
import et.gov.ethgov.land.domain.parcel.TenureType;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterParcelUseCase {

    private final ParcelRepository parcels;
    private final ParcelNumberSequence sequences;
    private final LandEventPublisher eventPublisher;
    private final Clock clock;

    public RegisterParcelUseCase(
            ParcelRepository parcels,
            ParcelNumberSequence sequences,
            LandEventPublisher eventPublisher,
            Clock clock) {
        this.parcels = parcels;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public ParcelId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        ParcelNumber parcelNumber = ParcelNumber.allocate(year, sequences.nextSequence(tenantId, year));
        Instant leaseExpiresAt = command.leaseExpiresAt() == null || command.leaseExpiresAt().isBlank()
                ? null
                : Instant.parse(command.leaseExpiresAt());
        LandParcel parcel = LandParcel.register(
                tenantId,
                parcelNumber,
                command.region(),
                command.zone(),
                command.woreda(),
                command.kebele(),
                command.locality(),
                LandUse.valueOf(command.landUse()),
                TenureType.valueOf(command.tenureType()),
                AreaSquareMeters.of(new BigDecimal(command.areaSquareMeters())),
                ParcelOwner.of(command.citizenId(), command.organizationId()),
                command.titleDeedNumber(),
                command.leaseReference(),
                leaseExpiresAt,
                command.linkedCaseId(),
                clock.instant());
        parcels.save(parcel);
        eventPublisher.publishAll(parcel.pullDomainEvents());
        return parcel.id();
    }

    public record Command(
            String tenantId,
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            String landUse,
            String tenureType,
            String areaSquareMeters,
            String citizenId,
            String organizationId,
            String titleDeedNumber,
            String leaseReference,
            String leaseExpiresAt,
            String linkedCaseId) {}
}
