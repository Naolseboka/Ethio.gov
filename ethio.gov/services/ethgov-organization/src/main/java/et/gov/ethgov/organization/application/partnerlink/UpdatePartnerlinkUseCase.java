package et.gov.ethgov.organization.application.partnerlink;

import et.gov.ethgov.organization.domain.partnerlink.Partnerlink;
import et.gov.ethgov.organization.domain.partnerlink.PartnerlinkId;
import et.gov.ethgov.organization.domain.partnerlink.PartnerlinkRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdatePartnerlinkUseCase {
    private final PartnerlinkRepository repository;
    private final Clock clock;

    public UpdatePartnerlinkUseCase(PartnerlinkRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PartnerlinkId execute(UpdatePartnerlinkCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PartnerlinkId id = PartnerlinkId.parse(command.partnerlinkId());
        Partnerlink aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("partnerlink not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdatePartnerlinkCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String partnerlinkId,
            String reason) {}
}
