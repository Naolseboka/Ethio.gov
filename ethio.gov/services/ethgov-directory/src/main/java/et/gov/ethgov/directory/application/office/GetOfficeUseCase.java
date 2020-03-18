package et.gov.ethgov.directory.application.office;

import et.gov.ethgov.directory.domain.office.Office;
import et.gov.ethgov.directory.domain.office.OfficeId;
import et.gov.ethgov.directory.domain.office.OfficeRepository;
import java.util.Objects;

public final class GetOfficeUseCase {
    private final OfficeRepository repository;

    public GetOfficeUseCase(OfficeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Office execute(String rawId) {
        OfficeId id = OfficeId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("office not found: " + rawId));
    }
}
