package et.gov.ethgov.case.application.party;

import et.gov.ethgov.case.domain.party.Party;
import et.gov.ethgov.case.domain.party.PartyId;
import et.gov.ethgov.case.domain.party.PartyRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class CreatePartyUseCase {
    private final PartyRepository repository;
    private final Clock clock;

    public CreatePartyUseCase(PartyRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public PartyId execute(CreatePartyCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        Party aggregate = Party.create(
                command.referenceCode(),
                command.title(),
                command.description(),
                command.tenantId(),
                now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record CreatePartyCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String partyId,
            String reason) {}
}
