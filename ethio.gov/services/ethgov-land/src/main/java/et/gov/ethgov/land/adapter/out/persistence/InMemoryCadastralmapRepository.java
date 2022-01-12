package et.gov.ethgov.land.adapter.out.persistence;

import et.gov.ethgov.land.domain.cadastralmap.Cadastralmap;
import et.gov.ethgov.land.domain.cadastralmap.CadastralmapId;
import et.gov.ethgov.land.domain.cadastralmap.CadastralmapRepository;
import et.gov.ethgov.land.domain.cadastralmap.CadastralmapStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Cadastralmap (replaceable with JPA). */
public final class InMemoryCadastralmapRepository implements CadastralmapRepository {
    private final Map<UUID, StoredCadastralmap> store = new ConcurrentHashMap<>();

    @Override
    public Cadastralmap save(Cadastralmap aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCadastralmap.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Cadastralmap> findById(CadastralmapId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCadastralmap::toDomain);
    }

    @Override
    public Optional<Cadastralmap> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCadastralmap::toDomain)
                .findFirst();
    }

    @Override
    public List<Cadastralmap> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCadastralmap row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCadastralmap::toDomain)
                .toList();
    }

    @Override
    public void delete(CadastralmapId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCadastralmap(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCadastralmap from(Cadastralmap aggregate) {
            return new StoredCadastralmap(
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

        Cadastralmap toDomain() {
            return Cadastralmap.rehydrate(
                    CadastralmapId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CadastralmapStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
