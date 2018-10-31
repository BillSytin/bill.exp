package chat.core.io;

import chat.core.data.Message;
import chat.core.data.MessageProcessingAction;
import chat.core.data.MessageProcessingManager;
import chat.core.data.MessageProcessingState;

import java.nio.channels.CompletionHandler;

public interface Session {

    long getId();
    void close();
    void submit(Message message, CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler);
    MessageProcessingManager getProcessingManager();

    default void submit(Message message) {

        submit(message, null);
    }
}
