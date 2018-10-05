package bill.exp.chat.core.data;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

@SuppressWarnings("unused")
public class BaseJoinBuffersMessageProcessor implements MessageProcessor {

    public static final int Order = MessageProcessorCategory.InputBuffer + MessageProcessorBaseOrder.First + 1;

    @Override
    public void process(MessageProcessingState state, CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler) {

        boolean isComplete = true;

        if (state.getIncomingMessage() instanceof ByteBufferMessage) {

            final ByteBuffer incoming = ((ByteBufferMessage) state.getIncomingMessage()).getBuffer();
            final int endPosition = incoming.limit() - 1;
            if (endPosition >= 0) {

                if (incoming.get(endPosition) == 0) {

                    incoming.position(endPosition);
                    incoming.flip();
                } else {

                    isComplete = false;
                }
            }

            ByteBuffer resulting;
            if (state.getProcessingMessage() instanceof ByteBufferMessage) {

                final ByteBuffer processing = ((ByteBufferMessage) state.getProcessingMessage()).getBuffer();

                incoming.rewind();
                processing.rewind();

                resulting = ByteBuffer
                        .allocate(processing.remaining() + incoming.remaining())
                        .put(processing)
                        .put(incoming);

            } else {

                resulting = incoming;
            }
            state.setProcessingMessage(new ByteBufferMessage(resulting, !isComplete));
        }

        completionHandler.completed(isComplete ? MessageProcessingAction.Next : MessageProcessingAction.Reset, state);
    }
}
