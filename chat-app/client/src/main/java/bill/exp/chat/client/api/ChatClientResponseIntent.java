package bill.exp.chat.client.api;

import bill.exp.chat.core.api.ResponseIntent;
import bill.exp.chat.model.ChatBaseAction;
import bill.exp.chat.model.ChatClientEnvelope;

public class ChatClientResponseIntent implements ResponseIntent {

    private final ChatBaseAction action;
    private final ChatClientEnvelope content;

    public ChatClientResponseIntent(ChatBaseAction action, ChatClientEnvelope content) {

        this.action = action;
        this.content = content;
    }

    public ChatBaseAction getAction() {

        return action;
    }

    public ChatClientEnvelope getContent() {

        return content;
    }
}
