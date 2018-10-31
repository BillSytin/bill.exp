package chat.client.api;

import chat.core.api.Request;
import chat.core.api.Response;
import chat.core.model.ChatServerEnvelope;

public interface ChatClientModelConverter {

    ChatClientRequestIntent convertRequestToIntent(Request request);
    ChatServerEnvelope[] convertIntentToModels(ChatClientRequestIntent intent);
    Response convertIntentToResponse(ChatClientResponseIntent intent);
}
