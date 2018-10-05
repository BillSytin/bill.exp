package bill.exp.chat.client.api;

import bill.exp.chat.core.api.RequestIntent;
import bill.exp.chat.model.ChatBaseAction;

public class ChatClientRequestIntent implements RequestIntent {

    private final ChatBaseAction action;
    private final String content;

    public ChatClientRequestIntent(ChatBaseAction action, String content) {

        this.action = action;
        this.content = content;
    }

    public ChatBaseAction getAction() {
        return action;
    }

    public String getContent() {
        return content;
    }
}
