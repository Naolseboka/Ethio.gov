package et.gov.ethgov.platform.messaging.inbox;

import et.gov.ethgov.platform.commons.tenant.TenantId;
import et.gov.ethgov.platform.persistence.inbox.InboxMessageEntity;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Ensures a CloudEvent / integration message is processed at most once per consumer.
 */
public final class InboxGuard {

    private final InboxStore store;

    public InboxGuard(InboxStore store) {
        this.store = Objects.requireNonNull(store, "store");
    }

    public boolean processOnce(
            String eventId,
            TenantId tenantId,
            String consumerName,
            String eventType,
            Runnable handler) {
        Objects.requireNonNull(eventId, "eventId");
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(consumerName, "consumerName");
        Objects.requireNonNull(eventType, "eventType");
        Objects.requireNonNull(handler, "handler");

        if (store.exists(eventId, consumerName)) {
            return false;
        }
        handler.run();
        store.save(InboxMessageEntity.processed(eventId, tenantId, consumerName, eventType));
        return true;
    }

    public <T> boolean processOnce(
            String eventId,
            TenantId tenantId,
            String consumerName,
            String eventType,
            T payload,
            Consumer<T> handler) {
        return processOnce(eventId, tenantId, consumerName, eventType, () -> handler.accept(payload));
    }

    public interface InboxStore {
        boolean exists(String eventId, String consumerName);

        void save(InboxMessageEntity entity);
    }
}
