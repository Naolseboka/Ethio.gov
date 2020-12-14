package et.gov.ethgov.gateway.application.circuitbreaker;

import et.gov.ethgov.gateway.domain.circuitbreaker.Circuitbreaker;
import et.gov.ethgov.gateway.domain.circuitbreaker.CircuitbreakerId;
import et.gov.ethgov.gateway.domain.circuitbreaker.CircuitbreakerRepository;
import java.util.Objects;

public final class GetCircuitbreakerUseCase {
    private final CircuitbreakerRepository repository;

    public GetCircuitbreakerUseCase(CircuitbreakerRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public Circuitbreaker execute(String rawId) {
        CircuitbreakerId id = CircuitbreakerId.parse(rawId);
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("circuitbreaker not found: " + rawId));
    }
}
