package bill.exp.chat.core.data;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public class BaseEncodeOutputMessageProcessor implements MessageProcessor {

    public static final int Order = MessageProcessorCategory.OutputConvert + MessageProcessorBaseOrder.First + 1;

    @Override
    public void process(MessageProcessingState state, CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler) {

        if (state.getOutputMessage() instanceof StringMessage) {

            final String[] outputStrings = ((StringMessage) state.getOutputMessage()).getStrings();
            final ByteBuffer[] outputBuffers = new ByteBuffer[outputStrings.length];
            for (int i = 0; i < outputStrings.length; i++) {

                final String outputString = outputStrings[i];
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
                outputBuffers[i] = outputBuffer;
            }

            state.setOutputMessage(new ByteBufferMessage(outputBuffers));
        }

        completionHandler.completed(MessageProcessingAction.Next, state);
    }
}
