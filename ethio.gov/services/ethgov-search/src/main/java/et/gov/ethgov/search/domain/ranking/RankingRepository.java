package et.gov.ethgov.search.domain.ranking;

import java.util.List;
import java.util.Optional;

public interface RankingRepository {
    Ranking save(Ranking aggregate);

    Optional<Ranking> findById(RankingId id);

    Optional<Ranking> findByReferenceCode(String tenantId, String referenceCode);

    List<Ranking> findByTenant(String tenantId, int limit);

    void delete(RankingId id);
}
