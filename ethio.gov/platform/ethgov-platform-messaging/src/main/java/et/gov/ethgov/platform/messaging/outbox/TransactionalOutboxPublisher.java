package et.gov.ethgov.platform.messaging.outbox;

import et.gov.ethgov.platform.persistence.outbox.OutboxMessageEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;

/**
 * In-process outbox publisher that collects messages for the current transaction.
 * Service modules supply a persistence adapter that flushes collected rows.
 */
public class TransactionalOutboxPublisher implements OutboxPublisher {

    private final OutboxMessageWriter writer;
    private final ThreadLocal<List<OutboxMessageEntity>> pending = ThreadLocal.withInitial(ArrayList::new);

    public TransactionalOutboxPublisher(OutboxMessageWriter writer) {
        this.writer = Objects.requireNonNull(writer, "writer");
    }

    @Override
    public void append(OutboxAppendCommand command) {
        pending.get().add(command.toEntity());
    }

    @Transactional
    public void flush() {
        List<OutboxMessageEntity> batch = List.copyOf(pending.get());
        pending.get().clear();
        if (!batch.isEmpty()) {
            writer.writeAll(batch);
        }
    }

    public void clear() {
        pending.remove();
    }

    @FunctionalInterface
    public interface OutboxMessageWriter {
        void writeAll(List<OutboxMessageEntity> messages);
    }
}
