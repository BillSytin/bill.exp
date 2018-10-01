package bill.exp.chat.server.data;

import bill.exp.chat.core.data.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

@Component
@Order(10)
public class DecodeBufferMessageProcessor implements MessageProcessor {

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
