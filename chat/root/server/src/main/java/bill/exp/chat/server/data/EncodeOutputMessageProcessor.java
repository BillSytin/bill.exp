package bill.exp.chat.server.data;

import bill.exp.chat.core.data.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

@Component
@Order(1000)
public class EncodeOutputMessageProcessor implements MessageProcessor {

    @Override
    public void process(MessageProcessingState state, CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler) {

        if (state.getProcessingMessage() instanceof StringMessage) {
            final String outputString = ((StringMessage) state.getProcessingMessage()).getString();
            final ByteBuffer encodeBuffer = StandardCharsets.UTF_8.encode(outputString);

            ByteBuffer outputBuffer;
            encodeBuffer.rewind();
            final int endPosition = encodeBuffer.limit();
            if (endPosition < encodeBuffer.capacity()) {
                outputBuffer = encodeBuffer;
                outputBuffer.limit(endPosition + 1);
                outputBuffer.position(endPosition);
            }
            else {
                outputBuffer = ByteBuffer
                        .allocate(encodeBuffer.remaining() + 1)
                        .put(encodeBuffer);
            }

            outputBuffer.put((byte) 0);
            outputBuffer.flip();

            state.setOutputBuffer(outputBuffer);

            completionHandler.completed(MessageProcessingAction.REPLY, state);
        }
        else {
            completionHandler.completed(MessageProcessingAction.NEXT, state);
        }
    }
}
