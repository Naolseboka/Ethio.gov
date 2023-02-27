package et.gov.ethgov.opendata.application.apicontract;

import et.gov.ethgov.opendata.domain.apicontract.Apicontract;
import et.gov.ethgov.opendata.domain.apicontract.ApicontractId;
import et.gov.ethgov.opendata.domain.apicontract.ApicontractRepository;
import java.util.Objects;

public final class GetApicontractUseCase {
    private final ApicontractRepository repository;

    public GetApicontractUseCase(ApicontractRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Apicontract execute(String rawId) {
        ApicontractId id = ApicontractId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("apicontract not found: " + rawId));
    }
}
