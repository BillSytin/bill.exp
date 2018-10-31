package chat.client.api;

import chat.core.api.ResponseIntent;
import chat.core.model.ChatAction;
import chat.core.model.ChatClientEnvelope;

public class ChatClientResponseIntent implements ResponseIntent {

    private final ChatAction action;
    private final ChatClientEnvelope[] content;

    public ChatClientResponseIntent(ChatAction action, ChatClientEnvelope[] content) {

        this.action = action;
        this.content = content;
    }

    public ChatAction getAction() {

        return action;
    }

    public ChatClientEnvelope[] getContent() {

        return content;
    }
}
