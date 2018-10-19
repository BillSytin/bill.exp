package bill.exp.chat.client.api;

import bill.exp.chat.core.api.ResponseIntent;
import bill.exp.chat.model.ChatAction;
import bill.exp.chat.model.ChatClientEnvelope;

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
