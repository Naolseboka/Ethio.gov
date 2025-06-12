package et.gov.ethgov.search.application.ranking;

import et.gov.ethgov.search.domain.ranking.Ranking;
import et.gov.ethgov.search.domain.ranking.RankingId;
import et.gov.ethgov.search.domain.ranking.RankingRepository;
import java.util.Objects;

public final class GetRankingUseCase {
    private final RankingRepository repository;

    public GetRankingUseCase(RankingRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Ranking execute(String rawId) {
        RankingId id = RankingId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ranking not found: " + rawId));
    }
}
