package chat.client.api;

import chat.core.api.RequestIntent;
import chat.core.model.ChatAction;

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
