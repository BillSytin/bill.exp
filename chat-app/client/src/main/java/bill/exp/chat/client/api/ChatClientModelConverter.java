package bill.exp.chat.client.api;

import bill.exp.chat.core.api.Request;
import bill.exp.chat.core.api.Response;
import bill.exp.chat.model.ChatServerEnvelope;

public interface ChatClientModelConverter {

    ChatClientRequestIntent convertRequestToIntent(Request request);
    ChatServerEnvelope[] convertIntentToModels(ChatClientRequestIntent intent);
    Response convertIntentToResponse(ChatClientResponseIntent intent);
}
