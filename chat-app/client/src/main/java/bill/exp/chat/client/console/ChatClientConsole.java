package bill.exp.chat.client.console;

import bill.exp.chat.model.ChatMessage;

@SuppressWarnings("unused")
public interface ChatClientConsole {

    void printOutput(ChatMessage message);
    ChatMessage readInput();
}