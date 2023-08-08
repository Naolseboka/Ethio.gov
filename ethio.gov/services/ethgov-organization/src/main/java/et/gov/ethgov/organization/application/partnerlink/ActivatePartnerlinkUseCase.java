package et.gov.ethgov.organization.application.partnerlink;

import et.gov.ethgov.organization.domain.partnerlink.Partnerlink;
import et.gov.ethgov.organization.domain.partnerlink.PartnerlinkId;
import et.gov.ethgov.organization.domain.partnerlink.PartnerlinkRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivatePartnerlinkUseCase {
    private final PartnerlinkRepository repository;
    private final Clock clock;

    public ActivatePartnerlinkUseCase(PartnerlinkRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PartnerlinkId execute(ActivatePartnerlinkCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PartnerlinkId id = PartnerlinkId.parse(command.partnerlinkId());
        Partnerlink aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("partnerlink not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivatePartnerlinkCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String partnerlinkId,
            String reason) {}
}
