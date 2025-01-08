package et.gov.ethgov.revenue.application.arrears;

import et.gov.ethgov.revenue.domain.arrears.Arrears;
import et.gov.ethgov.revenue.domain.arrears.ArrearsId;
import et.gov.ethgov.revenue.domain.arrears.ArrearsRepository;
import java.util.Objects;

public final class GetArrearsUseCase {
    private final ArrearsRepository repository;

    public GetArrearsUseCase(ArrearsRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Arrears execute(String rawId) {
        ArrearsId id = ArrearsId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("arrears not found: " + rawId));
    }
}
