package et.gov.ethgov.gis.application.feature;

import et.gov.ethgov.gis.application.GisEventPublisher;
import et.gov.ethgov.gis.domain.feature.FeatureId;
import et.gov.ethgov.gis.domain.feature.FeatureNumber;
import et.gov.ethgov.gis.domain.feature.FeatureNumberSequence;
import et.gov.ethgov.gis.domain.feature.FeatureRepository;
import et.gov.ethgov.gis.domain.feature.FeatureType;
import et.gov.ethgov.gis.domain.feature.GisFeature;
import et.gov.ethgov.gis.domain.feature.WktGeometry;
import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.commons.time.EthGovClocks;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterFeatureUseCase {

    private final FeatureRepository features;
    private final FeatureNumberSequence sequences;
    private final GisEventPublisher eventPublisher;
    private final Clock clock;

    public RegisterFeatureUseCase(
            FeatureRepository features,
            FeatureNumberSequence sequences,
            GisEventPublisher eventPublisher,
            Clock clock) {
        this.features = features;
        this.sequences = sequences;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public FeatureId execute(Command command) {
        Objects.requireNonNull(command, "command");
        TenantId tenantId = TenantId.of(command.tenantId());
        int year = LocalDate.now(EthGovClocks.ADDIS_ABABA).getYear();
        FeatureNumber featureNumber = FeatureNumber.allocate(year, sequences.nextSequence(tenantId, year));
        GisFeature feature = GisFeature.register(
                tenantId,
                featureNumber,
                FeatureType.valueOf(command.type()),
                command.name(),
                command.description(),
                WktGeometry.of(command.wkt()),
                command.parcelId(),
                command.permitId(),
                clock.instant());
        features.save(feature);
        eventPublisher.publishAll(feature.pullDomainEvents());
        return feature.id();
    }

    public record Command(
            String tenantId,
            String type,
            String name,
            String description,
            String wkt,
            String parcelId,
            String permitId) {}
}
