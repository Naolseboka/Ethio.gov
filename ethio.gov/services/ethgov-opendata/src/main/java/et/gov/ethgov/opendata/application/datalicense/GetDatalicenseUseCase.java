package et.gov.ethgov.opendata.application.datalicense;

import et.gov.ethgov.opendata.domain.datalicense.Datalicense;
import et.gov.ethgov.opendata.domain.datalicense.DatalicenseId;
import et.gov.ethgov.opendata.domain.datalicense.DatalicenseRepository;
import java.util.Objects;

public final class GetDatalicenseUseCase {
    private final DatalicenseRepository repository;

    public GetDatalicenseUseCase(DatalicenseRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Datalicense execute(String rawId) {
        DatalicenseId id = DatalicenseId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("datalicense not found: " + rawId));
    }
}
