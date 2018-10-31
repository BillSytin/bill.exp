package chat.server.cmd;

import chat.core.io.Session;
import chat.core.model.*;
import chat.server.users.ChatServerUser;

@SuppressWarnings("unused")
public class ChatServerCommandProcessingContext {

    private final Session session;
    private final ChatAction intentAction;
    private final ChatClientEnvelope input;
    private final ChatServerEnvelope output;
    private ChatMessage processingMessage;
    private ChatServerUser user;
    private boolean isCompleted;
    private ChatServerCommandProcessingManager processingManager;

    public ChatServerCommandProcessingContext(Session session, ChatAction intentAction, ChatClientEnvelope input) {

        this.session = session;
        this.intentAction = intentAction;
        this.input = input;
        this.output = new ChatServerEnvelope();
        this.output.setMessages(new ChatMessageList());
    }

    public ChatServerCommandProcessingManager getProcessingManager() {

        return processingManager;
    }

    public void setProcessingManager(ChatServerCommandProcessingManager processingManager) {

        this.processingManager = processingManager;
    }

    public boolean isCompleted() {

        return isCompleted;
    }

    public void setCompleted() {

        isCompleted = true;
    }

    public Session getSession() {

        return session;
    }

    public ChatServerUser getUser() {

        return user;
    }

    public void setUser(ChatServerUser user) {

        this.user = user;
    }

    public ChatAction getIntentAction() {

        return intentAction;
    }

    public ChatClientEnvelope getInput() {

        return input;
    }

    public ChatServerEnvelope getOutput() {

        return output;
    }

    public ChatMessage getProcessingMessage() {

        return processingMessage;
    }

    public void setProcessingMessage(ChatMessage processingMessage) {

        this.processingMessage = processingMessage;
    }
}
