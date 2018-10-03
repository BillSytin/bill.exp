package bill.exp.chat.core.data;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class BaseDecodeBufferMessageProcessor implements MessageProcessor {
    @Override
    public void process(MessageProcessingState state, CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler) {

        if (state.getProcessingMessage() instanceof ByteBufferMessage) {
            if (!((ByteBufferMessage) state.getProcessingMessage()).getIsIncomplete()) {
                final ByteBuffer input = ((ByteBufferMessage) state.getProcessingMessage()).getBuffer();
                final String output = StandardCharsets.UTF_8.decode(input).toString();
                state.setProcessingMessage(new StringMessage(output));
            }
        }

        completionHandler.completed(MessageProcessingAction.NEXT, state);
    }
}
