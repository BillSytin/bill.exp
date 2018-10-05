package bill.exp.chat.server.api;

import bill.exp.chat.core.api.ResponseIntent;
import bill.exp.chat.model.ChatBaseAction;
import bill.exp.chat.model.ChatServerEnvelope;

public class ChatServerResponseIntent implements ResponseIntent {

    private final ChatBaseAction action;
    private final ChatServerEnvelope content;

    public ChatServerResponseIntent(ChatBaseAction action, ChatServerEnvelope content) {

        this.action = action;
        this.content = content;
    }

    public ChatBaseAction getAction() {
        return action;
    }

    public ChatServerEnvelope getContent() {
        return content;
    }
}
