package et.gov.ethgov.case.application.party;

import et.gov.ethgov.case.domain.party.Party;
import et.gov.ethgov.case.domain.party.PartyId;
import et.gov.ethgov.case.domain.party.PartyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class SuspendPartyUseCase {
    private final PartyRepository repository;
    private final Clock clock;

    public SuspendPartyUseCase(PartyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PartyId execute(SuspendPartyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        PartyId id = PartyId.parse(command.partyId());
        Party aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("party not found"));
        aggregate.suspend(command.reason(), now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record SuspendPartyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String partyId,
            String reason) {}
}
