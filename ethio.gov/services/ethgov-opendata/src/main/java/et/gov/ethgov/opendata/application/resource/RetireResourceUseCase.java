package et.gov.ethgov.opendata.application.resource;

import et.gov.ethgov.opendata.application.OpenDataEventPublisher;
import et.gov.ethgov.opendata.domain.resource.DatasetResource;
import et.gov.ethgov.opendata.domain.resource.ResourceId;
import et.gov.ethgov.opendata.domain.resource.ResourceRepository;
import et.gov.ethgov.platform.commons.error.DomainException;
import java.time.Clock;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetireResourceUseCase {

    private final ResourceRepository resources;
    private final OpenDataEventPublisher eventPublisher;
    private final Clock clock;

    public RetireResourceUseCase(
            ResourceRepository resources, OpenDataEventPublisher eventPublisher, Clock clock) {
        this.resources = resources;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Transactional
    public void execute(String resourceId, String reason) {
        Objects.requireNonNull(resourceId, "resourceId");
        DatasetResource resource = resources
                .findById(ResourceId.of(resourceId))
                .orElseThrow(() -> new DomainException(
                        "RESOURCE_NOT_FOUND", "Resource Not Found", "Resource does not exist", 404));
        resource.retire(reason, clock.instant());
        resources.save(resource);
        eventPublisher.publishResourceEvents(resource.pullDomainEvents());
    }
}
