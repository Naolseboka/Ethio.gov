package et.gov.ethgov.directory.application.openinghours;

import et.gov.ethgov.directory.domain.openinghours.Openinghours;
import et.gov.ethgov.directory.domain.openinghours.OpeninghoursId;
import et.gov.ethgov.directory.domain.openinghours.OpeninghoursRepository;
import java.util.Objects;

public final class GetOpeninghoursUseCase {
    private final OpeninghoursRepository repository;

    public GetOpeninghoursUseCase(OpeninghoursRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Openinghours execute(String rawId) {
        OpeninghoursId id = OpeninghoursId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("openinghours not found: " + rawId));
    }
}
