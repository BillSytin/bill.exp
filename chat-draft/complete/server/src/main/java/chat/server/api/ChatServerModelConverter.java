package chat.server.api;

import chat.core.api.Request;
import chat.core.api.Response;
import chat.core.model.ChatClientEnvelope;

interface ChatServerModelConverter {

    ChatServerRequestIntent convertRequestToIntent(Request request);
    ChatClientEnvelope[] convertIntentToModels(ChatServerRequestIntent intent);
    Response convertIntentToResponse(ChatServerResponseIntent intent);
}
