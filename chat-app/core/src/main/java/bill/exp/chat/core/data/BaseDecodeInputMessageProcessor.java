package bill.exp.chat.core.data;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public class BaseDecodeInputMessageProcessor implements MessageProcessor {

    public static final int Order = MessageProcessorCategory.InputConvert + MessageProcessorBaseOrder.First + 1;

    @Override
    public void process(MessageProcessingState state, CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler) {

        if (state.getProcessingMessage() instanceof ByteBufferMessage) {
            if (!((ByteBufferMessage) state.getProcessingMessage()).isIncomplete()) {
                final ByteBuffer input = ((ByteBufferMessage) state.getProcessingMessage()).getBuffer();
                input.rewind();
                final String output = StandardCharsets.UTF_8.decode(input).toString();
                state.setProcessingMessage(new StringMessage(output));
            }
        }

        completionHandler.completed(MessageProcessingAction.Next, state);
    }
}
