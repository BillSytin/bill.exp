package chat.server.msg;

import chat.core.util.Stoppable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Iterator;

@SuppressWarnings("unused")
@Repository
public class DefaultChatServerMessagesRepository implements ChatServerMessagesRepository, Stoppable {

    private final ChatServerMessageNotificationsService notificationsService;
    private final ChatServerMessageRecord[] records;
    private long stampSequence;

    @Autowired
    public DefaultChatServerMessagesRepository(ChatServerMessagesRepositoryConfig config, ChatServerMessageNotificationsService notificationsService) {

        this.records = new ChatServerMessageRecord[config.getMaxMessageCount()];
        this.stampSequence = records.length;
        this.notificationsService = notificationsService;
    }

    @Override
    public long put(ChatServerMessageRecord record) {

        final long stamp = putRecord(record);
        notificationsService.fireNewMessageNotification(stamp);
        return stamp;
    }

    private synchronized long putRecord(ChatServerMessageRecord record) {

        final long stamp = stampSequence;
        record.setStamp(stamp);
        records[(int)(stamp % records.length)] = record;
        stampSequence++;
        return stamp;
    }

    @Override
    public synchronized Iterable<ChatServerMessageRecord> getAllSince(long stamp) {

        final int length = this.records.length;
        final long currentStamp = stampSequence - length;
        final ChatServerMessageRecord[] records = new ChatServerMessageRecord[length];
        System.arraycopy(this.records, 0, records, 0, length);

        return new RecordsIterable(records, stamp == 0 || (stamp + length < currentStamp) ? currentStamp : stamp);
    }

    @Override
    public boolean isStopping() {

        return false;
    }

    @Override
    public void setStopping() {

        if (notificationsService instanceof Stoppable)
            ((Stoppable) notificationsService).setStopping();
    }

    @Override
    public void setStopped() {

    }

    @Override
    public boolean waitStopped(int timeout) {

        return true;
    }

    private static class RecordsIterable implements Iterable<ChatServerMessageRecord> {

        private final ChatServerMessageRecord[] records;
        private final long stamp;

        public RecordsIterable(ChatServerMessageRecord[] records, long stamp) {

            this.records = records;
            this.stamp = stamp;

        }

        @Override
        public Iterator<ChatServerMessageRecord> iterator() {

            return new RecordsIterator(records, stamp);
        }
    }

    private static class RecordsIterator implements Iterator<ChatServerMessageRecord> {

        private final ChatServerMessageRecord[] records;
        private long stamp;
        private final int start;
        private final int length;
        private int index;
        private ChatServerMessageRecord record;

        public RecordsIterator(ChatServerMessageRecord[] records, long stamp) {

            this.records = records;
            this.stamp = stamp;

            length = records.length;
            start = (int)(stamp % length);
            index = 0;
            moveNext();
        }

        private void moveNext() {

            record = null;
            while (index < length) {

                final ChatServerMessageRecord current = records[(start + index) % length];
                index++;
                if (current != null) {

                    final long currentStamp = current.getStamp();
                    if (currentStamp >= stamp) {

                        stamp = currentStamp;
                        record = current;
                    }
                    break;
                }
            }
        }

        @Override
        public boolean hasNext() {

            return record != null;
        }

        @Override
        public ChatServerMessageRecord next() {

            ChatServerMessageRecord result = record;
            moveNext();
            return result;
        }
    }
}
