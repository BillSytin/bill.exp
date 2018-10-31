package chat.core.data;

import java.nio.channels.CompletionHandler;

public interface MessageProcessor {
    void process(MessageProcessingState state, CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler);
}
