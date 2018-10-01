package bill.exp.chat.core.data;

import bill.exp.chat.core.io.Session;

import java.nio.channels.CompletionHandler;

public class MessageProcessingState {
    private final Session session;
    private final Message incomingMessage;
    private final MessageProcessor processor;
    private CompletionHandler<MessageProcessingAction, MessageProcessingState> finalCompletionHandler;
    private Message processingMessage;

    public MessageProcessingState(Session session, Message incomingMessage, MessageProcessor processor) {
        this.session = session;
        this.incomingMessage = incomingMessage;
        this.processingMessage = null;
        this.processor = processor;
        this.finalCompletionHandler = null;
    }

    public Message getIncomingMessage() { return incomingMessage; }

    public Message getProcessingMessage() { return processingMessage; }

    public void setProcessingMessage(Message value) { processingMessage = value; }

    public MessageProcessor getProcessor() { return processor; }

    public CompletionHandler<MessageProcessingAction, MessageProcessingState> getFinalCompletionHandler() { return finalCompletionHandler; }
    public void setFinalCompletionHandler(CompletionHandler<MessageProcessingAction, MessageProcessingState> value) { finalCompletionHandler = value; }
}
