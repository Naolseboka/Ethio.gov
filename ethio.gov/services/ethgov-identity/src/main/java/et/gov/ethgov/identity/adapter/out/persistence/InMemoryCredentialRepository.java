package et.gov.ethgov.identity.adapter.out.persistence;

import et.gov.ethgov.identity.domain.credential.Credential;
import et.gov.ethgov.identity.domain.credential.CredentialId;
import et.gov.ethgov.identity.domain.credential.CredentialRepository;
import et.gov.ethgov.identity.domain.credential.CredentialStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Credential (replaceable with JPA). */
public final class InMemoryCredentialRepository implements CredentialRepository {
    private final Map<UUID, StoredCredential> store = new ConcurrentHashMap<>();

    @Override
    public Credential save(Credential aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredCredential.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Credential> findById(CredentialId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredCredential::toDomain);
    }

    @Override
    public Optional<Credential> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredCredential::toDomain)
                .findFirst();
    }

    @Override
    public List<Credential> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredCredential row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredCredential::toDomain)
                .toList();
    }

    @Override
    public void delete(CredentialId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredCredential(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredCredential from(Credential aggregate) {
            return new StoredCredential(
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

        Credential toDomain() {
            return Credential.rehydrate(
                    CredentialId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    CredentialStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
