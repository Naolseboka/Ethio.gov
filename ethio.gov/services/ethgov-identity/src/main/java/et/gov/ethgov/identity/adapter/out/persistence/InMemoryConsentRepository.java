package et.gov.ethgov.identity.adapter.out.persistence;

import et.gov.ethgov.identity.domain.consent.Consent;
import et.gov.ethgov.identity.domain.consent.ConsentId;
import et.gov.ethgov.identity.domain.consent.ConsentRepository;
import et.gov.ethgov.identity.domain.consent.ConsentStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Consent (replaceable with JPA). */
public final class InMemoryConsentRepository implements ConsentRepository {
    private final Map<UUID, StoredConsent> store = new ConcurrentHashMap<>();

    @Override
    public Consent save(Consent aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredConsent.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Consent> findById(ConsentId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredConsent::toDomain);
    }

    @Override
    public Optional<Consent> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredConsent::toDomain)
                .findFirst();
    }

    @Override
    public List<Consent> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredConsent row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredConsent::toDomain)
                .toList();
    }

    @Override
    public void delete(ConsentId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredConsent(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredConsent from(Consent aggregate) {
            return new StoredConsent(
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

        Consent toDomain() {
            return Consent.rehydrate(
                    ConsentId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    ConsentStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
