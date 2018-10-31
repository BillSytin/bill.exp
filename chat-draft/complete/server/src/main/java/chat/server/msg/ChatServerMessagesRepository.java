package chat.server.msg;

@SuppressWarnings("unused")
public interface ChatServerMessagesRepository {

    long put(ChatServerMessageRecord record);
    Iterable<ChatServerMessageRecord> getAllSince(long stamp);
}
