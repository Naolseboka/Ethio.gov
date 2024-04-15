package et.gov.ethgov.permits.application.permit;

import et.gov.ethgov.permits.application.PermitsEventPublisher;
import et.gov.ethgov.permits.domain.permit.ConstructionPermit;
import et.gov.ethgov.permits.domain.permit.FloorAreaSquareMeters;
import et.gov.ethgov.permits.domain.permit.PermitApplicant;
import et.gov.ethgov.permits.domain.permit.PermitId;
import et.gov.ethgov.permits.domain.permit.PermitNumber;
import et.gov.ethgov.permits.domain.permit.PermitNumberSequence;
import et.gov.ethgov.permits.domain.permit.PermitRepository;
import et.gov.ethgov.permits.domain.permit.PermitType;
import et.gov.ethgov.permits.domain.permit.StructureUse;
import et.gov.ethgov.platform.commons.money.Money;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplyPermitUseCase {

    private final PermitRepository permits;
    private final PermitNumberSequence sequences;
    private final PermitsEventPublisher eventPublisher;
    private final Clock clock;

    public ApplyPermitUseCase(
            PermitRepository permits,
            PermitNumberSequence sequences,
            PermitsEventPublisher eventPublisher,
            Clock clock) {
        this.permits = permits;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public PermitId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        PermitNumber permitNumber = PermitNumber.allocate(year, sequences.nextSequence(tenantId, year));
        Money cost = command.estimatedCostEtb() == null || command.estimatedCostEtb().isBlank()
                ? null
                : Money.etb(new BigDecimal(command.estimatedCostEtb()));
        ConstructionPermit permit = ConstructionPermit.apply(
                tenantId,
                permitNumber,
                PermitType.valueOf(command.type()),
                StructureUse.valueOf(command.structureUse()),
                command.projectName(),
                command.description(),
                PermitApplicant.of(command.citizenId(), command.organizationId()),
                command.parcelId(),
                command.floors(),
                FloorAreaSquareMeters.of(new BigDecimal(command.floorAreaSquareMeters())),
                cost,
                command.region(),
                command.zone(),
                command.woreda(),
                command.kebele(),
                command.locality(),
                command.linkedCaseId(),
                clock.instant());
        permits.save(permit);
        eventPublisher.publishAll(permit.pullDomainEvents());
        return permit.id();
    }

    public record Command(
            String tenantId,
            String type,
            String structureUse,
            String projectName,
            String description,
            String citizenId,
            String organizationId,
            String parcelId,
            int floors,
            String floorAreaSquareMeters,
            String estimatedCostEtb,
            String region,
            String zone,
            String woreda,
            String kebele,
            String locality,
            String linkedCaseId) {}
}
