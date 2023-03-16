package et.gov.ethgov.opendata.application.harvest;

import et.gov.ethgov.opendata.domain.harvest.Harvest;
import et.gov.ethgov.opendata.domain.harvest.HarvestId;
import et.gov.ethgov.opendata.domain.harvest.HarvestRepository;
import java.util.Objects;

public final class GetHarvestUseCase {
    private final HarvestRepository repository;

    public GetHarvestUseCase(HarvestRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Harvest execute(String rawId) {
        HarvestId id = HarvestId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("harvest not found: " + rawId));
    }
}
