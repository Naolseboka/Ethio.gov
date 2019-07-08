package et.gov.ethgov.citizen.application.namechange;

import et.gov.ethgov.citizen.domain.namechange.Namechange;
import et.gov.ethgov.citizen.domain.namechange.NamechangeId;
import et.gov.ethgov.citizen.domain.namechange.NamechangeRepository;
import java.util.Objects;

public final class GetNamechangeUseCase {
    private final NamechangeRepository repository;

    public GetNamechangeUseCase(NamechangeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Namechange execute(String rawId) {
        NamechangeId id = NamechangeId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("namechange not found: " + rawId));
    }
}
