package bill.exp.chat.core.data;

import java.nio.channels.CompletionHandler;
import java.util.Arrays;

@SuppressWarnings("unused")
public class BaseMessageProcessingManager implements MessageProcessingManager {

    private final MessageProcessor[] processors;

    public BaseMessageProcessingManager(MessageProcessor[] processors) {
        this.processors = processors;
    }

    @Override
    public MessageProcessingChain createProcessingChain() {

        return new ProcessingChain(processors);
    }

    @Override
    public Iterable<MessageProcessor> getProcessors() {

        return Arrays.asList(processors);
    }

    private static class ProcessingChain implements MessageProcessingChain, CompletionHandler<MessageProcessingAction, MessageProcessingState> {

        private final MessageProcessor[] processors;
        private int current;

        public ProcessingChain(MessageProcessor[] processors) {

            this.processors = processors;
            this.current = 0;
        }

        private void processNext(MessageProcessingState state) {

            if (current < processors.length) {

                processors[current].process(state, this);
            }
            else {

                state.getFinalCompletionHandler().completed(MessageProcessingAction.Done, state);
            }
        }

        @Override
        public void process(MessageProcessingState state, CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler) {

            state.setFinalCompletionHandler(completionHandler);
            processNext(state);
        }

        @Override
        public void completed(MessageProcessingAction result, MessageProcessingState attachment) {

            switch (result) {
                case Reset:
                    current = 0;
                    attachment.getFinalCompletionHandler().completed(result, attachment);
                    break;
                case Done:
                    current = processors.length;
                    processNext(attachment);
                    break;
                case Async:
                    attachment.getFinalCompletionHandler().completed(result, attachment);
                    break;
                case Next:
                    current++;
                    processNext(attachment);
                    break;
            }
        }

        @Override
        public void failed(Throwable exc, MessageProcessingState attachment) {

            attachment.getFinalCompletionHandler().failed(exc, attachment);
        }
    }
}
