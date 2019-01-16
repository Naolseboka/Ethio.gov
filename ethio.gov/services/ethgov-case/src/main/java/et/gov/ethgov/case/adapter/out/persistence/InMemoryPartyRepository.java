package et.gov.ethgov.case.adapter.out.persistence;

import et.gov.ethgov.case.domain.party.Party;
import et.gov.ethgov.case.domain.party.PartyId;
import et.gov.ethgov.case.domain.party.PartyRepository;
import et.gov.ethgov.case.domain.party.PartyStatus;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory persistence adapter for Party (replaceable with JPA). */
public final class InMemoryPartyRepository implements PartyRepository {
    private final Map<UUID, StoredParty> store = new ConcurrentHashMap<>();

    @Override
    public Party save(Party aggregate) {
        Objects.requireNonNull(aggregate, "aggregate");
        store.put(aggregate.id().value(), StoredParty.from(aggregate));
        return aggregate;
    }

    @Override
    public Optional<Party> findById(PartyId id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(store.get(id.value())).map(StoredParty::toDomain);
    }

    @Override
    public Optional<Party> findByReferenceCode(String tenantId, String referenceCode) {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(referenceCode, "referenceCode");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId) && row.referenceCode.equals(referenceCode))
                .map(StoredParty::toDomain)
                .findFirst();
    }

    @Override
    public List<Party> findByTenant(String tenantId, int limit) {
        Objects.requireNonNull(tenantId, "tenantId");
        return store.values().stream()
                .filter(row -> row.tenantId.equals(tenantId))
                .sorted(Comparator.comparing((StoredParty row) -> row.updatedAt).reversed())
                .limit(limit)
                .map(StoredParty::toDomain)
                .toList();
    }

    @Override
    public void delete(PartyId id) {
        Objects.requireNonNull(id, "id");
        store.remove(id.value());
    }

    private record StoredParty(
            UUID id,
            String referenceCode,
            String title,
            String description,
            String tenantId,
            String status,
            Instant createdAt,
            Instant updatedAt,
            long version) {
        static StoredParty from(Party aggregate) {
            return new StoredParty(
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

        Party toDomain() {
            return Party.rehydrate(
                    PartyId.of(id),
                    referenceCode,
                    title,
                    description,
                    tenantId,
                    PartyStatus.valueOf(status),
                    createdAt,
                    updatedAt,
                    version);
        }
    }
}
