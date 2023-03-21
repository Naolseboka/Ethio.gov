package et.gov.ethgov.opendata.application.opendataset;

import et.gov.ethgov.opendata.domain.opendataset.Opendataset;
import et.gov.ethgov.opendata.domain.opendataset.OpendatasetId;
import et.gov.ethgov.opendata.domain.opendataset.OpendatasetRepository;
import java.util.Objects;

public final class GetOpendatasetUseCase {
    private final OpendatasetRepository repository;

    public GetOpendatasetUseCase(OpendatasetRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Opendataset execute(String rawId) {
        OpendatasetId id = OpendatasetId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("opendataset not found: " + rawId));
    }
}
