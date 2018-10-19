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

            final ByteBuffer[] incomingBuffers = ((ByteBufferMessage) state.getIncomingMessage()).getBuffers();
            final ByteBuffer incoming = incomingBuffers[incomingBuffers.length - 1];
            final int endPosition = incoming.limit() - 1;
            if (endPosition >= 0) {

                if (incoming.get(endPosition) == 0) {

                    incoming.position(endPosition);
                    incoming.flip();
                } else {

                    isComplete = false;
                }
            }

            ByteBuffer[] resulting;
            if (state.getProcessingMessage() instanceof ByteBufferMessage) {

                final ByteBuffer[] processingBuffers = ((ByteBufferMessage) state.getProcessingMessage()).getBuffers();
                final ByteBuffer processing = processingBuffers[processingBuffers.length - 1];

                incoming.rewind();
                processing.rewind();

                resulting = new ByteBuffer[processingBuffers.length];
                if ((resulting.length - 1) > 0) {

                    System.arraycopy(processingBuffers, 0, resulting, 0, resulting.length - 1);
                }

                resulting[resulting.length - 1] = ByteBuffer
                        .allocate(processing.remaining() + incoming.remaining())
                        .put(processing)
                        .put(incoming);

            } else {

                resulting = incomingBuffers;
            }
            state.setProcessingMessage(new ByteBufferMessage(resulting, !isComplete));
        }

        completionHandler.completed(isComplete ? MessageProcessingAction.Next : MessageProcessingAction.Reset, state);
    }
}
