package chat.client.console;

import chat.core.model.ChatMessage;

@SuppressWarnings("unused")
public interface ChatClientConsole {

    @SuppressWarnings("EmptyMethod")
    void create();
    void printOutput(ChatMessage message);
    ChatMessage readInput(boolean loginPrompt);
}
