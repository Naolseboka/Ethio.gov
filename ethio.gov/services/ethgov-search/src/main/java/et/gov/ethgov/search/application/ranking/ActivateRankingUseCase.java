package et.gov.ethgov.search.application.ranking;

import et.gov.ethgov.search.domain.ranking.Ranking;
import et.gov.ethgov.search.domain.ranking.RankingId;
import et.gov.ethgov.search.domain.ranking.RankingRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public final class ActivateRankingUseCase {
    private final RankingRepository repository;
    private final Clock clock;

    public ActivateRankingUseCase(RankingRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RankingId execute(ActivateRankingCommand command) {
        Objects.requireNonNull(command, "command");
        Instant now = clock.instant();
        RankingId id = RankingId.parse(command.rankingId());
        Ranking aggregate = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ranking not found"));
        aggregate.activate(now);
        repository.save(aggregate);
        return aggregate.id();
    }

    public record ActivateRankingCommand(
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String rankingId,
            String reason) {}
}
