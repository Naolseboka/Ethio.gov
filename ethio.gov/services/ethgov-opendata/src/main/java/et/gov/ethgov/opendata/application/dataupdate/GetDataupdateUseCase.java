package et.gov.ethgov.opendata.application.dataupdate;

import et.gov.ethgov.opendata.domain.dataupdate.Dataupdate;
import et.gov.ethgov.opendata.domain.dataupdate.DataupdateId;
import et.gov.ethgov.opendata.domain.dataupdate.DataupdateRepository;
import java.util.Objects;

public final class GetDataupdateUseCase {
    private final DataupdateRepository repository;

    public GetDataupdateUseCase(DataupdateRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Dataupdate execute(String rawId) {
        DataupdateId id = DataupdateId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("dataupdate not found: " + rawId));
    }
}
