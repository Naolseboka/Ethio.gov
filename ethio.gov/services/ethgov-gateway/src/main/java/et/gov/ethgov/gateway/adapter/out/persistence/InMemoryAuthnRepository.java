package et.gov.ethgov.gateway.adapter.out.persistence;

import et.gov.ethgov.gateway.domain.authn.Authn;
import et.gov.ethgov.gateway.domain.authn.AuthnId;
import et.gov.ethgov.gateway.domain.authn.AuthnRepository;
import et.gov.ethgov.gateway.domain.authn.AuthnStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Authn (replaceable with JPA). */
public final class InMemoryAuthnRepository implements AuthnRepository {
    private final Map<UUID, StoredAuthn> store = new ConcurrentHashMap<>();

    @Override
    public Authn save(Authn aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredAuthn.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Authn> findById(AuthnId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredAuthn::toDomain);
    }

    @Override
    public Optional<Authn> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredAuthn::toDomain)
                .findFirst();
    }

    @Override
    public List<Authn> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredAuthn row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredAuthn::toDomain)
                .toList();
    }

    @Override
    public void delete(AuthnId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredAuthn(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredAuthn from(Authn aggregate) {
            return new StoredAuthn(
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

        Authn toDomain() {
            return Authn.rehydrate(
                    AuthnId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    AuthnStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
