package et.gov.ethgov.developer.application.apiproduct;

import et.gov.ethgov.developer.domain.apiproduct.Apiproduct;
import et.gov.ethgov.developer.domain.apiproduct.ApiproductRepository;
import java.util.List;
import java.util.Objects;

public final class ListApiproductUseCase {
    private final ApiproductRepository repository;

    public ListApiproductUseCase(ApiproductRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Apiproduct> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
