package et.gov.ethgov.land.application.title;

import et.gov.ethgov.land.domain.title.Title;
import et.gov.ethgov.land.domain.title.TitleId;
import et.gov.ethgov.land.domain.title.TitleRepository;
import java.util.Objects;

public final class GetTitleUseCase {
    private final TitleRepository repository;

    public GetTitleUseCase(TitleRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Title execute(String rawId) {
        TitleId id = TitleId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("title not found: " + rawId));
    }
}
