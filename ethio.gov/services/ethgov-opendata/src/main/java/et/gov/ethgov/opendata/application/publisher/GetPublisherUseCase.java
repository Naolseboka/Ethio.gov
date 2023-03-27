package et.gov.ethgov.opendata.application.publisher;

import et.gov.ethgov.opendata.domain.publisher.Publisher;
import et.gov.ethgov.opendata.domain.publisher.PublisherId;
import et.gov.ethgov.opendata.domain.publisher.PublisherRepository;
import java.util.Objects;

public final class GetPublisherUseCase {
    private final PublisherRepository repository;

    public GetPublisherUseCase(PublisherRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Publisher execute(String rawId) {
        PublisherId id = PublisherId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("publisher not found: " + rawId));
    }
}
