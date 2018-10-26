package bill.exp.chat.core.data;

import org.springframework.context.annotation.Scope;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

@SuppressWarnings("unused")
@Scope("prototype")
public class BaseJoinBuffersMessageProcessor implements MessageProcessor {

    public static final int Order = MessageProcessorCategory.InputBuffer + MessageProcessorBaseOrder.First + 1;
    private ByteBuffer[] pendingBuffers;

    private ByteBuffer[] detachPendingBuffers() {

        ByteBuffer[] result = pendingBuffers;
        if (result != null) {

            pendingBuffers = null;
        }
        return result;
    }

    private void setPendingBuffers(ByteBuffer[] pendingBuffers) {

        this.pendingBuffers = pendingBuffers;
    }

    @SuppressWarnings("ManualArrayCopy")
    @Override
    public void process(MessageProcessingState state, CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler) {

        boolean isComplete = true;

        if (state.getInputMessage() instanceof ByteBufferMessage) {

            final ByteBuffer[] processingBuffers = detachPendingBuffers();
            final ByteBuffer[] incomingBuffers = ((ByteBufferMessage) state.getInputMessage()).getBuffers();
            final ByteBuffer incomingLast = incomingBuffers[incomingBuffers.length - 1];
            final int endPosition = incomingLast.limit() - 1;
            if (endPosition >= 0) {

                if (incomingLast.get(endPosition) == 0) {

                    incomingLast.position(endPosition);
                    incomingLast.flip();
                } else {

                    isComplete = false;
                }
            }

            ByteBuffer[] resulting;
            if (processingBuffers != null) {

                final int insertPos = processingBuffers.length - 1;
                final ByteBuffer processingLast = processingBuffers[insertPos];
                final ByteBuffer incomingFirst = incomingBuffers[0];

                incomingFirst.rewind();
                processingLast.rewind();

                resulting = new ByteBuffer[insertPos + incomingBuffers.length];
                for (int i = 0; i < insertPos; i++)
                    resulting[i] = processingBuffers[i];

                final ByteBuffer resultingMiddle = ByteBuffer
                        .allocate(processingLast.remaining() + incomingFirst.remaining())
                        .put(processingLast)
                        .put(incomingFirst);
                resultingMiddle.rewind();
                resulting[insertPos] = resultingMiddle;

                for (int i = 1; i < incomingBuffers.length; i++) {
                    resulting[i + insertPos] = incomingBuffers[i];
                }
            } else {

                resulting = incomingBuffers;
            }

            if (isComplete) {

                state.setInputMessage(new ByteBufferMessage(resulting));
            }
            else {

                setPendingBuffers(resulting);
                state.setInputMessage(null);
            }
        }

        completionHandler.completed(isComplete ? MessageProcessingAction.Next : MessageProcessingAction.Reset, state);
    }
}
