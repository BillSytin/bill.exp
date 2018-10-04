package bill.exp.chat.core.data;

import java.nio.channels.CompletionHandler;

public abstract class BaseMessageProcessingManager implements MessageProcessingManager {

    abstract protected MessageProcessor[] getProcessors();

    @Override
    public MessageProcessor createProcessor() {
        return new ProcessingIterator(getProcessors());
    }

    private class ProcessingIterator implements MessageProcessor, CompletionHandler<MessageProcessingAction, MessageProcessingState> {

        private final MessageProcessor[] processors;
        private int current;

        public ProcessingIterator(MessageProcessor[] processors) {
            this.processors = processors;
            this.current = 0;
        }

        private void processNext(MessageProcessingState state) {
            if (current < processors.length) {
                processors[current].process(state, this);
            }
            else {
                state.getFinalCompletionHandler().completed(MessageProcessingAction.DONE, state);
            }
        }

        @Override
        public void process(MessageProcessingState state, CompletionHandler<MessageProcessingAction, MessageProcessingState> completionHandler) {
            state.setFinalCompletionHandler(completionHandler);
            processNext(state);
        }

        @Override
        public void completed(MessageProcessingAction result, MessageProcessingState attachment) {
            current++;
            switch (result) {
                case REPLY:
                    attachment.getFinalCompletionHandler().completed(result, attachment);
                    processNext(attachment);
                    break;
                case RESET:
                    current = 0;
                    attachment.getFinalCompletionHandler().completed(result, attachment);
                    break;
                case NEXT:
                default:
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
