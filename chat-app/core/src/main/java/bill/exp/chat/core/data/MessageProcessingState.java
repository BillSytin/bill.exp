package bill.exp.chat.core.data;

import bill.exp.chat.core.io.Session;

import java.nio.channels.CompletionHandler;

public class MessageProcessingState {
    private final Session session;
    private Message incomingMessage;
    private CompletionHandler<MessageProcessingAction, MessageProcessingState> finalCompletionHandler;
    private Message processingMessage;
    private Message outputMessage;

    public MessageProcessingState(Session session, Message incomingMessage) {
        this.session = session;
        this.incomingMessage = incomingMessage;
        this.processingMessage = null;
        this.finalCompletionHandler = null;
        this.outputMessage = null;
    }

    public Session getSession() { return session; }

    public Message getIncomingMessage() { return incomingMessage; }
    public void setIncomingMessage(Message value) { incomingMessage = value; }

    public Message getProcessingMessage() { return processingMessage; }
    public void setProcessingMessage(Message value) { processingMessage = value; }

    public CompletionHandler<MessageProcessingAction, MessageProcessingState> getFinalCompletionHandler() { return finalCompletionHandler; }
    public void setFinalCompletionHandler(CompletionHandler<MessageProcessingAction, MessageProcessingState> value) { finalCompletionHandler = value; }

    public Message getOutputMessage() { return outputMessage; }
    public void setOutputMessage(Message value) { outputMessage = value; }
}
