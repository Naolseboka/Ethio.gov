package et.gov.ethgov.case.application.party;

import et.gov.ethgov.case.domain.party.Party;
import et.gov.ethgov.case.domain.party.PartyId;
import et.gov.ethgov.case.domain.party.PartyRepository;
import java.util.Objects;

public final class GetPartyUseCase {
    private final PartyRepository repository;

    public GetPartyUseCase(PartyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Party execute(String rawId) {
        PartyId id = PartyId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("party not found: " + rawId));
    }
}
