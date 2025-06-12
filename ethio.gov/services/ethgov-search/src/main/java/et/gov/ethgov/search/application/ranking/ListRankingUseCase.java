package et.gov.ethgov.search.application.ranking;

import et.gov.ethgov.search.domain.ranking.Ranking;
import et.gov.ethgov.search.domain.ranking.RankingRepository;
import java.util.List;
import java.util.Objects;

public final class ListRankingUseCase {
    private final RankingRepository repository;

    public ListRankingUseCase(RankingRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<Ranking> execute(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return repository.findByTenant(tenantId, safeLimit);
    }
}
