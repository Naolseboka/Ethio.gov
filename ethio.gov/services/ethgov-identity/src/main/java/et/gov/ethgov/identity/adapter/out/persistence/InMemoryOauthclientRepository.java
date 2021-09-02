package et.gov.ethgov.identity.adapter.out.persistence;

import et.gov.ethgov.identity.domain.oauthclient.Oauthclient;
import et.gov.ethgov.identity.domain.oauthclient.OauthclientId;
import et.gov.ethgov.identity.domain.oauthclient.OauthclientRepository;
import et.gov.ethgov.identity.domain.oauthclient.OauthclientStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Oauthclient (replaceable with JPA). */
public final class InMemoryOauthclientRepository implements OauthclientRepository {
    private final Map<UUID, StoredOauthclient> store = new ConcurrentHashMap<>();

    @Override
    public Oauthclient save(Oauthclient aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredOauthclient.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Oauthclient> findById(OauthclientId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredOauthclient::toDomain);
    }

    @Override
    public Optional<Oauthclient> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredOauthclient::toDomain)
                .findFirst();
    }

    @Override
    public List<Oauthclient> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredOauthclient row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredOauthclient::toDomain)
                .toList();
    }

    @Override
    public void delete(OauthclientId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredOauthclient(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredOauthclient from(Oauthclient aggregate) {
            return new StoredOauthclient(
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

        Oauthclient toDomain() {
            return Oauthclient.rehydrate(
                    OauthclientId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    OauthclientStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
