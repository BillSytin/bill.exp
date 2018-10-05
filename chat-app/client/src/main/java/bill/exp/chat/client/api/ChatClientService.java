package bill.exp.chat.client.api;

import bill.exp.chat.core.io.Session;
import bill.exp.chat.model.ChatServerEnvelope;

@SuppressWarnings("unused")
public interface ChatClientService {

    @SuppressWarnings({"RedundantThrows", "SameReturnValue"})
    ChatClientResponseIntent process(Session session, ChatClientRequestIntent intent, ChatServerEnvelope model);
    boolean isAsyncIntent(ChatClientRequestIntent intent);
}
