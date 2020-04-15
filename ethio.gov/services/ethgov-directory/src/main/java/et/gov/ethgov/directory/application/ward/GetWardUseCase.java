package et.gov.ethgov.directory.application.ward;

import et.gov.ethgov.directory.domain.ward.Ward;
import et.gov.ethgov.directory.domain.ward.WardId;
import et.gov.ethgov.directory.domain.ward.WardRepository;
import java.util.Objects;

public final class GetWardUseCase {
    private final WardRepository repository;

    public GetWardUseCase(WardRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Ward execute(String rawId) {
        WardId id = WardId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ward not found: " + rawId));
    }
}
