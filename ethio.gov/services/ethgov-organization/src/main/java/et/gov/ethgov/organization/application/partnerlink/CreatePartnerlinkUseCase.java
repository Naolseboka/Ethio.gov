package et.gov.ethgov.organization.application.partnerlink;

import et.gov.ethgov.organization.domain.partnerlink.Partnerlink;
import et.gov.ethgov.organization.domain.partnerlink.PartnerlinkId;
import et.gov.ethgov.organization.domain.partnerlink.PartnerlinkRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePartnerlinkUseCase {
    private final PartnerlinkRepository repository;
    private final Clock clock;

    public CreatePartnerlinkUseCase(PartnerlinkRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PartnerlinkId execute(CreatePartnerlinkCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Partnerlink aggregate = Partnerlink.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePartnerlinkCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String partnerlinkId,
            String reason) {}
}
