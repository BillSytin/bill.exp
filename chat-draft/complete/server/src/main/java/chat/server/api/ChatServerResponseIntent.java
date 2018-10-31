package chat.server.api;

import chat.core.api.ResponseIntent;
import chat.core.model.ChatAction;
import chat.core.model.ChatServerEnvelope;

public class ChatServerResponseIntent implements ResponseIntent {

    private final ChatAction action;
    private final ChatServerEnvelope[] content;

    public ChatServerResponseIntent(ChatAction action, ChatServerEnvelope[] content) {

        this.action = action;
        this.content = content;
    }

    public ChatAction getAction() {
        return action;
    }

    public ChatServerEnvelope[] getContent() {
        return content;
    }
}
