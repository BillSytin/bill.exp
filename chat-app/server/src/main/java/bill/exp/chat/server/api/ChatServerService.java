package bill.exp.chat.server.api;

import bill.exp.chat.core.io.Session;
import bill.exp.chat.core.model.ChatClientEnvelope;

@SuppressWarnings({"unused", "SameReturnValue"})
public interface ChatServerService {

    ChatServerResponseIntent process(Session session, ChatServerRequestIntent intent, ChatClientEnvelope[] model);
    boolean isAsyncIntent(ChatServerRequestIntent intent);
    void dispose(Session session);
}
