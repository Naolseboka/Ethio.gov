package et.gov.ethgov.case.adapter.out.persistence;

import et.gov.ethgov.case.domain.casenote.Casenote;
import et.gov.ethgov.case.domain.casenote.CasenoteId;
import et.gov.ethgov.case.domain.casenote.CasenoteRepository;
import et.gov.ethgov.case.domain.casenote.CasenoteStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Casenote (replaceable with JPA). */
public final class InMemoryCasenoteRepository implements CasenoteRepository {
    private final Map<UUID, StoredCasenote> store = new ConcurrentHashMap<>();

    @Override
    public Casenote save(Casenote aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCasenote.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Casenote> findById(CasenoteId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCasenote::toDomain);
    }

    @Override
    public Optional<Casenote> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCasenote::toDomain)
                .findFirst();
    }

    @Override
    public List<Casenote> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCasenote row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCasenote::toDomain)
                .toList();
    }

    @Override
    public void delete(CasenoteId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCasenote(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCasenote from(Casenote aggregate) {
            return new StoredCasenote(
                    aggregate.id().value(),
                    aggregate.referenceCode(),
                    aggregate.title(),
                    aggregate.description(),
                    aggregate.tenantId(),
                    aggregate.status().name(),
                    aggregate.createdAt(),
                    aggregate.updatedAt(),
                    aggregate.version());
        }

        Casenote toDomain() {
            return Casenote.rehydrate(
                    CasenoteId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CasenoteStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
