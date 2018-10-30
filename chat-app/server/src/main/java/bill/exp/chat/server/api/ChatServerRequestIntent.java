package bill.exp.chat.server.api;

import bill.exp.chat.core.api.RequestIntent;
import bill.exp.chat.core.model.ChatAction;
import bill.exp.chat.core.model.ChatClientEnvelope;

public class ChatServerRequestIntent implements RequestIntent {

    private final ChatAction action;
    private final String[] content;

    private final ChatClientEnvelope[] models;

    public ChatServerRequestIntent(ChatAction action, String[] content) {

        this.action = action;
        this.content = content;
        this.models = null;
    }

    public ChatServerRequestIntent(ChatAction action, ChatClientEnvelope[] models) {

        this.action = action;
        this.content = null;
        this.models = models;
    }

    public ChatAction getAction() {
        return action;
    }

    public ChatClientEnvelope[] getModels() {
        return models;
    }

    public String[] getContent() {
        return content;
    }
}
