package bill.exp.chat.core.data;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class BaseDecodeInputMessageProcessor implements MessageProcessor {

    public static final int Order = MessageProcessorCategory.InputConvert + MessageProcessorBaseOrder.First + 1;

    @Override
    public void process(MessageProcessingState state, CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler) {

        if (state.getProcessingMessage() instanceof ByteBufferMessage) {
            if (!((ByteBufferMessage) state.getProcessingMessage()).isIncomplete()) {

                final ByteBuffer[] inputBuffers = ((ByteBufferMessage) state.getProcessingMessage()).getBuffers();
                final ArrayList<String> outputStrings = new ArrayList<>(inputBuffers.length);
                for (final ByteBuffer input : inputBuffers) {

                    int curPos = 0;
                    final int inputLength = input.limit();
                    for (int i = 0; i <= inputLength; i++) {

                        if (i == inputLength || input.get(i) == 0) {

                            input.limit(i);
                            input.position(curPos);
                            outputStrings.add(StandardCharsets.UTF_8.decode(input).toString());
                            input.limit(inputLength);
                            curPos = i + 1;
                        }
                    }
                }

                state.setProcessingMessage(new StringMessage(outputStrings.toArray(new String[0])));
            }
        }

        completionHandler.completed(MessageProcessingAction.Next, state);
    }
}
