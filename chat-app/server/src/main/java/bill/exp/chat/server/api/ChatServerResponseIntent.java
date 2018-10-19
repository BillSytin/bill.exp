package bill.exp.chat.server.api;

import bill.exp.chat.core.api.ResponseIntent;
import bill.exp.chat.model.ChatAction;
import bill.exp.chat.model.ChatServerEnvelope;

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
