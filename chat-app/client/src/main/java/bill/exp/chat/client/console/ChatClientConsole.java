package bill.exp.chat.client.console;

import bill.exp.chat.model.ChatMessage;

@SuppressWarnings("unused")
public interface ChatClientConsole {

    @SuppressWarnings("EmptyMethod")
    void create();
    void printOutput(ChatMessage message);
    ChatMessage readInput(boolean loginPrompt);
}
