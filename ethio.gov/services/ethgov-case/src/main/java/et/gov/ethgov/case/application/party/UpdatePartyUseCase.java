package et.gov.ethgov.case.application.party;

import et.gov.ethgov.case.domain.party.Party;
import et.gov.ethgov.case.domain.party.PartyId;
import et.gov.ethgov.case.domain.party.PartyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class UpdatePartyUseCase {
    private final PartyRepository repository;
    private final Clock clock;

    public UpdatePartyUseCase(PartyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PartyId execute(UpdatePartyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PartyId id = PartyId.parse(command.partyId());
        Party aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("party not found"));
        aggregate.updateDetails(command.title(), command.description(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record UpdatePartyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String partyId,
            String reason) {}
}
