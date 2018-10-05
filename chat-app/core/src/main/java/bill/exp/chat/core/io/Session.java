package bill.exp.chat.core.io;

import bill.exp.chat.core.data.Message;
import bill.exp.chat.core.data.MessageProcessingAction;
import bill.exp.chat.core.data.MessageProcessingState;

import java.nio.channels.CompletionHandler;

public interface Session {

    long getId();
    void close();
    void submit(Message message, CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler);

    default void submit(Message message) {

        submit(message, null);
    }
}
