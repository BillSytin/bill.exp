package chat.server.api;

import chat.core.io.Session;
import chat.core.model.ChatClientEnvelope;

@SuppressWarnings({"unused", "SameReturnValue"})
public interface ChatServerService {

    ChatServerResponseIntent process(Session session, ChatServerRequestIntent intent, ChatClientEnvelope[] model);
    boolean isAsyncIntent(ChatServerRequestIntent intent);
    void dispose(Session session);
}
