package et.gov.ethgov.opendata.application.quality;

import et.gov.ethgov.opendata.domain.quality.Quality;
import et.gov.ethgov.opendata.domain.quality.QualityId;
import et.gov.ethgov.opendata.domain.quality.QualityRepository;
import java.util.Objects;

public final class GetQualityUseCase {
    private final QualityRepository repository;

    public GetQualityUseCase(QualityRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Quality execute(String rawId) {
        QualityId id = QualityId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("quality not found: " + rawId));
    }
}
