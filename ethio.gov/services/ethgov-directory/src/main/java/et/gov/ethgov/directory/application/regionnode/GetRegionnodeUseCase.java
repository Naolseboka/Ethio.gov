package et.gov.ethgov.directory.application.regionnode;

import et.gov.ethgov.directory.domain.regionnode.Regionnode;
import et.gov.ethgov.directory.domain.regionnode.RegionnodeId;
import et.gov.ethgov.directory.domain.regionnode.RegionnodeRepository;
import java.util.Objects;

public final class GetRegionnodeUseCase {
    private final RegionnodeRepository repository;

    public GetRegionnodeUseCase(RegionnodeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Regionnode execute(String rawId) {
        RegionnodeId id = RegionnodeId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("regionnode not found: " + rawId));
    }
}
