package et.gov.ethgov.opendata.application.tag;

import et.gov.ethgov.opendata.domain.tag.Tag;
import et.gov.ethgov.opendata.domain.tag.TagId;
import et.gov.ethgov.opendata.domain.tag.TagRepository;
import java.util.Objects;

public final class GetTagUseCase {
    private final TagRepository repository;

    public GetTagUseCase(TagRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Tag execute(String rawId) {
        TagId id = TagId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("tag not found: " + rawId));
    }
}
