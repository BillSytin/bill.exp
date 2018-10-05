package bill.exp.chat.server.msg;

import java.util.Iterator;

@SuppressWarnings("unused")
public interface ChatServerMessagesRepository {

    long put(ChatServerMessageRecord record);
    Iterator<ChatServerMessageRecord> getAllSince(long stamp);
}
