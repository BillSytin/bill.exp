package chat.client.api;

import chat.core.io.Session;
import chat.core.model.ChatServerEnvelope;

@SuppressWarnings({"unused", "SameReturnValue"})
public interface ChatClientService {

    @SuppressWarnings({"RedundantThrows", "SameReturnValue"})
    ChatClientResponseIntent process(Session session, ChatClientRequestIntent intent, ChatServerEnvelope[] models);
    boolean isAsyncIntent(ChatClientRequestIntent intent);
    void dispose(Session session);
}
