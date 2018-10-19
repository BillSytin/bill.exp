package bill.exp.chat.client.api;

import bill.exp.chat.core.api.RequestIntent;
import bill.exp.chat.model.ChatAction;

public class ChatClientRequestIntent implements RequestIntent {

    private final ChatAction action;
    private final String[] content;

    public ChatClientRequestIntent(ChatAction action, String[] content) {

        this.action = action;
        this.content = content;
    }

    public ChatAction getAction() {
        return action;
    }

    public String[] getContent() {
        return content;
    }
}
