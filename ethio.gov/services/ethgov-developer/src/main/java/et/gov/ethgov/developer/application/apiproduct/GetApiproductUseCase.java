package et.gov.ethgov.developer.application.apiproduct;

import et.gov.ethgov.developer.domain.apiproduct.Apiproduct;
import et.gov.ethgov.developer.domain.apiproduct.ApiproductId;
import et.gov.ethgov.developer.domain.apiproduct.ApiproductRepository;
import java.util.Objects;

public final class GetApiproductUseCase {
    private final ApiproductRepository repository;

    public GetApiproductUseCase(ApiproductRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Apiproduct execute(String rawId) {
        ApiproductId id = ApiproductId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("apiproduct not found: " + rawId));
    }
}
