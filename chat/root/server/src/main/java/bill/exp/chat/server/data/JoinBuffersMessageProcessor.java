package bill.exp.chat.server.data;

import bill.exp.chat.core.data.ByteBufferMessage;
import bill.exp.chat.core.data.MessageProcessingAction;
import bill.exp.chat.core.data.MessageProcessingState;
import bill.exp.chat.core.data.MessageProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;

@Component
@Order(-10)
public class JoinBuffersMessageProcessor implements MessageProcessor {

    @Override
    public void process(MessageProcessingState state, CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler) {

        boolean isComplete = true;

        if (state.getIncomingMessage() instanceof ByteBufferMessage) {
            final ByteBuffer incoming = ((ByteBufferMessage) state.getIncomingMessage()).getBuffer();
            final int endPosition = incoming.position();
            if (incoming.get(endPosition) == 0) {
                incoming.position(endPosition - 1);
                incoming.flip();
            }
            else {
                isComplete = false;
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

        completionHandler.completed(isComplete ? MessageProcessingAction.NEXT : MessageProcessingAction.RESET, state);
    }
}
