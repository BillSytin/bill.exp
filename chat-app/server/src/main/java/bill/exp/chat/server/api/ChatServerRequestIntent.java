package bill.exp.chat.server.api;

import bill.exp.chat.core.api.RequestIntent;
import bill.exp.chat.model.ChatBaseAction;

public class ChatServerRequestIntent implements RequestIntent {

    private final ChatBaseAction action;
    private final String content;

    public ChatServerRequestIntent(ChatBaseAction action, String content) {

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
