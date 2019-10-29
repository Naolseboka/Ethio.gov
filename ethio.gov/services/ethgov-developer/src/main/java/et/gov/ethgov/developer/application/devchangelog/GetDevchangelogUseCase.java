package et.gov.ethgov.developer.application.devchangelog;

import et.gov.ethgov.developer.domain.devchangelog.Devchangelog;
import et.gov.ethgov.developer.domain.devchangelog.DevchangelogId;
import et.gov.ethgov.developer.domain.devchangelog.DevchangelogRepository;
import java.util.Objects;

public final class GetDevchangelogUseCase {
    private final DevchangelogRepository repository;

    public GetDevchangelogUseCase(DevchangelogRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Devchangelog execute(String rawId) {
        DevchangelogId id = DevchangelogId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("devchangelog not found: " + rawId));
    }
}
