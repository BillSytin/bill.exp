package bill.exp.chat.server.api;

import bill.exp.chat.core.api.Request;
import bill.exp.chat.core.api.Response;
import bill.exp.chat.core.model.ChatClientEnvelope;

public interface ChatServerModelConverter {

    ChatServerRequestIntent convertRequestToIntent(Request request);
    ChatClientEnvelope[] convertIntentToModels(ChatServerRequestIntent intent);
    Response convertIntentToResponse(ChatServerResponseIntent intent);
}
