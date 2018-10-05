package bill.exp.chat.server.api;

import bill.exp.chat.core.api.Request;
import bill.exp.chat.core.api.Response;
import bill.exp.chat.model.ChatClientEnvelope;

public interface ChatServerModelConverter {

    ChatServerRequestIntent convertRequestToIntent(Request request);
    ChatClientEnvelope convertIntentToModel(ChatServerRequestIntent intent);
    Response convertIntentToResponse(ChatServerResponseIntent intent);
}
